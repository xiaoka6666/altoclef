package adris.altoclef.util.helpers;

import net.minecraft.item.AxeItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.OnAStickItem;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShearsItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;

/**
 * 1.21.4 removed the {@code ToolItem} base class; tool detection now has to be
 * done against the concrete tool item types. This utility centralises that so
 * callers keep working without a per-call change list.
 */
public final class ToolItemDetect {

    private ToolItemDetect() {}

    public static boolean isToolItem(Item item) {
        return item instanceof SwordItem
                || item instanceof MiningToolItem
                || item instanceof AxeItem
                || item instanceof ShovelItem
                || item instanceof HoeItem
                || item instanceof ShearsItem
                || item instanceof PickaxeItem
                || item instanceof FishingRodItem
                || item instanceof OnAStickItem;
    }
}
