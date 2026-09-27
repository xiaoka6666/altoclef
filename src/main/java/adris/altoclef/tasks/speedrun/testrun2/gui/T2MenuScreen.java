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
