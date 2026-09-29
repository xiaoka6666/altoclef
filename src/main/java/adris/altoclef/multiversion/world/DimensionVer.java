package adris.altoclef.multiversion.world;

import adris.altoclef.multiversion.Pattern;
import net.minecraft.world.World;

/** 1.21.11 moved DimensionType's ultrawarm/natural flags into environment attributes; use the dimension key. */
public class DimensionVer {

    @Pattern
    private static boolean isUltrawarm(World world) {
        //#if MC >= 12111
        //$$ return world.getRegistryKey() == World.NETHER;
        //#else
        return world.getDimension().ultrawarm();
        //#endif
    }

    @Pattern
    private static boolean isNatural(World world) {
        //#if MC >= 12111
        //$$ return world.getRegistryKey() == World.OVERWORLD;
        //#else
        return world.getDimension().natural();
        //#endif
    }
}
