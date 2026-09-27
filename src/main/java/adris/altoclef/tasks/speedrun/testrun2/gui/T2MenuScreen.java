package adris.altoclef.tasks.speedrun.testrun2.gui;

import adris.altoclef.AltoClef;
import adris.altoclef.Debug;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * TenorClef control panel. Same floating-card language as Ostinato
 * (centered glass, sidebar, header chip, drop shadow) but a warm
 * amber/rose palette so the two menus stay distinct in a paired
 * session. Widgets are only the Agent text fields; everything else
 * is painted and hit-tested here.
 */
public class T2MenuScreen extends Screen {

    private static int tab;
    private static int pendingOpen;

    private static final String[][] TAB_TASKS_L = {
            {"testrun2  RSG", "testrun2"},
            {"aa  advancements", "aa"},
            {"t2core  reserve test", "t2core"},
            {"escape  nether tunnel", "escape"},
            {"escape here", "escape here"},
            {"village", "village"},
            {"zerocycle  pillar", "zerocycle"},
            {"groundzero  fountain", "groundzero"},
    };
    private static final String[][] TAB_TASKS_R = {
            {"sethome", "sethome"},
            {"home", "home"},
            {"back  death", "back"},
            {"t2doctor", "t2doctor"},
            {"logdump", "logdump"},
            {"PANIC", "t2panic"},
            {"stop", "stop"},
    };
    private static final String[][] TAB_LINK_L = {
            {"butler status", "butler"},
            {"fleet list", "fleet"},
            {"fleet ping", "fleet ping"},
    };
    private static final String[][] TAB_LINK_R = {
            {"xget list", "xget list"},
    };
    private static final String[][] TAB_MEDIA_L = {
            {"mapart add image", "mapart add"},
            {"mapart convert inbox", "mapart convert"},
            {"mapart print last", "mapart print"},
    };
    private static final String[][] TAB_MEDIA_R = {
            {"dj play", "dj play"},
            {"dj stop", "dj stop"},
    };
    private static final String[][] TAB_AGENT_L = {
            {"agent on", "agent on"},
            {"agent off", "agent off"},
    };
    private static final String[][] TAB_AGENT_R = {
            {"t2doctor", "t2doctor"},
            {"stop", "stop"},
    };

    private Object keyBox;
    private Object urlBox;
    private Object modelBox;
    private Object bindBox;
    private boolean dropProv;
    private boolean dropModel;
    private final List<int[]> hits = new ArrayList<>();
    private final List<String> hitCmd = new ArrayList<>();
    private final List<String> hitLab = new ArrayList<>();

    // Floating panel (GUI units). Filled by layout().
    private int px0, py0, px1, py1, headerB, sideR, contentX, contentY, contentW, footerT;

    public T2MenuScreen() {
        super(titleText());
    }

    public static void open() {
        pendingOpen = 0;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null) return;
        Runnable show = () -> {
            try {
                if (mc.currentScreen instanceof T2MenuScreen) return;
                T2MenuScreen screen = new T2MenuScreen();
                try {
                    mc.getClass().getMethod("openScreen", Screen.class).invoke(mc, screen);
                } catch (NoSuchMethodException e) {
                    mc.getClass().getMethod("setScreen", Screen.class).invoke(mc, screen);
                }
                Debug.logMessage("T2MENU opened");
            } catch (Throwable t) {
                Debug.logWarning("T2MENU open: " + t.getClass().getSimpleName() + " " + t.getMessage());
            }
        };
        try {
            mc.execute(show);
        } catch (Throwable t) {
            show.run();
        }
    }

    /** Chat closes the screen after the command. Wait it out. */
    public static void openSoon() {
        pendingOpen = 12;
        Debug.logMessage("T2MENU queued");
    }

    public static void poll() {
        if (pendingOpen <= 0) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null) return;
        if (mc.currentScreen != null) {
            String n = mc.currentScreen.getClass().getSimpleName();
            if (n.contains("Chat") || n.contains("Command")) return;
            if (mc.currentScreen instanceof T2MenuScreen) {
                pendingOpen = 0;
                return;
            }
        }
        pendingOpen--;
        if (pendingOpen <= 0) open();
    }

    private static Text titleText() {
        try {
            return (Text) Text.class.getMethod("literal", String.class).invoke(null, "TenorClef");
        } catch (Throwable t) {
            try {
                return (Text) Class.forName("net.minecraft.text.LiteralText")
                        .getConstructor(String.class).newInstance("TenorClef");
            } catch (Throwable t2) {
                throw new IllegalStateException(t2);
            }
        }
    }

    private String[][] tabLeft() {
        if (tab == 1) return TAB_LINK_L;
        if (tab == 2) return TAB_MEDIA_L;
        if (tab == 3) return TAB_AGENT_L;
        if (tab == 4) return new String[0][];
        return TAB_TASKS_L;
    }

    private String[][] tabRight() {
        if (tab == 1) return TAB_LINK_R;
        if (tab == 2) return TAB_MEDIA_R;
        if (tab == 3) return TAB_AGENT_R;
        if (tab == 4) return new String[0][];
        return TAB_TASKS_R;
    }

    private void layout() {
        int pw = Math.min(560, Math.max(420, this.width - 32));
        int ph = Math.min(tab == 3 ? 292 : 268, this.height - 16);
        if (ph < 200) ph = Math.max(188, this.height - 16);
        px0 = (this.width - pw) / 2;
        py0 = (this.height - ph) / 2;
        px1 = px0 + pw;
        py1 = py0 + ph;
        headerB = py0 + 28;
        footerT = py1 - 24;
        sideR = px0 + (pw >= 500 ? 118 : 100);
        contentX = sideR + 10;
        contentY = headerB + 8;
        contentW = px1 - contentX - 10;
    }
