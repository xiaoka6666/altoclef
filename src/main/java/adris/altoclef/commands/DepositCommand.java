package adris.altoclef.commands;

import adris.altoclef.AltoClef;
import adris.altoclef.commandsystem.*;
import adris.altoclef.tasks.container.StoreInAnyContainerTask;
import adris.altoclef.util.ItemTarget;
import adris.altoclef.util.helpers.ItemHelper;
import adris.altoclef.util.helpers.StorageHelper;
import adris.altoclef.util.slots.PlayerSlot;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.item.AxeItem;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.item.HoeItem;
import net.minecraft.registry.tag.ItemTags;

import net.minecraft.registry.tag.ItemTags;
import net.minecraft.item.ShearsItem;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.item.ShovelItem;
import net.minecraft.registry.tag.ItemTags;

import org.apache.commons.lang3.ArrayUtils;

public class DepositCommand extends Command {
    public DepositCommand() throws CommandException {
        super("deposit", "Deposit ALL of our items", new Arg(ItemList.class, "items (empty for ALL non gear items)", null, 0, false));
    }

    public static ItemTarget[] getAllNonEquippedOrToolItemsAsTarget(AltoClef mod) {
        return StorageHelper.getAllInventoryItemsAsTargets(slot -> {
            // Ignore armor
            if (ArrayUtils.contains(PlayerSlot.ARMOR_SLOTS, slot))
                return false;
            ItemStack stack = StorageHelper.getItemStackInSlot(slot);
            // Ignore tools
            if (!stack.isEmpty()) {
                Item item = stack.getItem();
                return !(ItemHelper.isSword(item))
                        && !(ItemHelper.isPickaxe(item))
                        && !(item instanceof ShovelItem)
                        && !(item instanceof HoeItem)
                        && !(item instanceof AxeItem)
                        && !(item instanceof ShearsItem);
            }
            return false;
        });
    }

    @Override
    protected void call(AltoClef mod, ArgParser parser) throws CommandException {
        ItemList itemList = parser.get(ItemList.class);
        ItemTarget[] items;
        if (itemList == null) {
            items = getAllNonEquippedOrToolItemsAsTarget(mod);
        } else {
            items = itemList.items;
        }

        mod.runUserTask(new StoreInAnyContainerTask(false, items), this::finish);
    }
}
