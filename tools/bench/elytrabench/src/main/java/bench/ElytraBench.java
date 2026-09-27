package bench;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalXZ;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

/**
 * Elytra bench for Ostinato main (1.21.11): gliding-ready at y=200 with an elytra and rockets,
 * Baritone's elytra process flies to far overworld goals. CSV: ticks, rockets, health.
 */
public class ElytraBench implements ClientModInitializer {
    private static boolean started;
    private static int worldTicks;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (started || mc.player == null || mc.getServer() == null) return;
            if (++worldTicks < 100) return;
            started = true;
            Thread t = new Thread(() -> {
                try {
                    run(mc, Integer.getInteger("elytrabench.reps", 1));
                } catch (Throwable e) {
                    log("failed: " + e);
                    e.printStackTrace();
                } finally {
                    if (Boolean.getBoolean("tenorclef.pathbench.exit")) mc.execute(mc::scheduleStop);
                }
            }, "ElytraBench");
            t.setDaemon(true);
            t.start();
        });
    }

    private static void log(String s) {
        System.out.println("ELYTRABENCH " + s);
    }

    private static double hdist(MinecraftClient mc, int x, int z) {
        double dx = mc.player.getX() - (x + 0.5), dz = mc.player.getZ() - (z + 0.5);
        return Math.sqrt(dx * dx + dz * dz);
    }

    private static void reset(MinecraftClient mc, int x, int y, int z) throws Exception {
        UUID id = mc.player.getUuid();
        mc.getServer().submit(() -> {
            ServerPlayerEntity sp = mc.getServer().getPlayerManager().getPlayer(id);
            if (sp == null) return;
            sp.getInventory().clear();
            sp.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.ELYTRA));
            sp.getInventory().setStack(0, new ItemStack(Items.FIREWORK_ROCKET, 64));
            sp.setHealth(sp.getMaxHealth());
            sp.getHungerManager().setFoodLevel(20);
            sp.setVelocity(0, 0, 0);
            sp.fallDistance = 0;
            sp.networkHandler.requestTeleport(x + 0.5, y, z + 0.5, 0, 0);
        }).get();
        for (int i = 0; i < 100 && (hdist(mc, x, z) > 1 || Math.abs(mc.player.getY() - y) > 2); i++) Thread.sleep(50);
    }

    private static void run(MinecraftClient mc, int reps) throws Exception {
        IBaritone baritone = BaritoneAPI.getProvider().getPrimaryBaritone();
        int ox = mc.player.getBlockX(), oz = mc.player.getBlockZ();
        int[][] offs = {{1000, 0}, {0, -1000}, {-700, 700}, {1500, 800}};
        long limitTicks = Long.getLong("elytrabench.limitTicks", 20L * 180);
        Path dir = Paths.get("pathbench");
        Files.createDirectories(dir);
        Path f = dir.resolve("pathbench_elytra_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");
        log("writing " + f.toAbsolutePath());
        int ok = 0, n = 0;
        try (PrintWriter csv = new PrintWriter(Files.newBufferedWriter(f))) {
            csv.println("goal,dx,dz,dist,rep,result,ticks,endDist,rockets,hp,minHp");
            for (int gi = 0; gi < offs.length; gi++) {
                int gx = ox + offs[gi][0], gz = oz + offs[gi][1];
                for (int r = 0; r < reps; r++) {
                    mc.submit(() -> baritone.getPathingBehavior().cancelEverything()).get();
                    if (mc.player.isDead()) { mc.execute(() -> mc.player.requestRespawn()); Thread.sleep(2000); }
                    reset(mc, ox, 200, oz);
                    Thread.sleep(300);
                    reset(mc, ox, 200, oz);
                    int rockets0 = mc.player.getInventory().count(Items.FIREWORK_ROCKET);
                    long t0 = mc.world.getTime();
                    mc.submit(() -> baritone.getElytraProcess().pathTo(new GoalXZ(gx, gz))).get();
                    String result = "TIMEOUT";
                    float minHp = mc.player.getHealth();
                    long lastLog = -1;
                    while (true) {
                        Thread.sleep(25);
                        long el = mc.world.getTime() - t0;
                        if (mc.player == null || mc.player.isDead()) { result = "DIED"; break; }
                        minHp = Math.min(minHp, mc.player.getHealth());
                        if (el / 100 != lastLog) {
                            lastLog = el / 100;
                            log(String.format(Locale.ROOT, "t=%d pos=%.0f,%.0f,%.0f d=%.0f glide=%s hp=%.1f", el, mc.player.getX(), mc.player.getY(), mc.player.getZ(), hdist(mc, gx, gz), mc.player.isGliding(), mc.player.getHealth()));
                        }
                        if (!baritone.getElytraProcess().isActive() && el > 40) { result = hdist(mc, gx, gz) < 48 ? "LANDED" : "STOPPED"; break; }
                        if (el > limitTicks) break;
                    }
                    mc.submit(() -> baritone.getPathingBehavior().cancelEverything()).get();
                    long ticks = mc.world.getTime() - t0;
                    boolean alive = mc.player != null && !mc.player.isDead();
                    int used = rockets0 - (alive ? mc.player.getInventory().count(Items.FIREWORK_ROCKET) : rockets0);
                    n++;
                    if (result.equals("LANDED")) ok++;
                    csv.println(String.format(Locale.ROOT, "%d,%d,%d,%d,%d,%s,%d,%.1f,%d,%.1f,%.1f", gi, offs[gi][0], offs[gi][1],
                            (int) Math.hypot(offs[gi][0], offs[gi][1]), r, result, ticks, alive ? hdist(mc, gx, gz) : -1, used,
                            alive ? mc.player.getHealth() : 0f, minHp));
                    csv.flush();
                    log("goal=" + gi + " rep=" + r + " " + result + " ticks=" + ticks + " rockets=" + used);
                }
            }
        }
        log(String.format(Locale.ROOT, "SUMMARY landRate=%d/%d", ok, n));
    }
}
