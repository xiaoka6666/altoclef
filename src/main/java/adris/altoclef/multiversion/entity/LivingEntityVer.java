package adris.altoclef.multiversion.entity;

import adris.altoclef.multiversion.Pattern;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class LivingEntityVer {


    // FIXME this should be possible with mappings, right?
    @Pattern
    private static Iterable<ItemStack> getItemsEquipped(LivingEntity entity) {
        //#if MC >= 12111
        //$$ return adris.altoclef.multiversion.entity.InventoryHelper.equippedItems(entity);
        //#elseif MC >= 12005
        return entity.getEquippedItems();
        //#else
        //$$ return entity.getItemsEquipped();
        //#endif
    }

    /** 1.21.11 removed the hand/armor item iterators; rebuilt from the equipment slots. */
    @Pattern
    private static Iterable<ItemStack> getHandItems(LivingEntity entity) {
        //#if MC >= 12111
        //$$ return adris.altoclef.multiversion.entity.InventoryHelper.handItems(entity);
        //#else
        return entity.getHandItems();
        //#endif
    }

    @Pattern
    private static Iterable<ItemStack> getArmorItems(LivingEntity entity) {
        //#if MC >= 12111
        //$$ return adris.altoclef.multiversion.entity.InventoryHelper.armorItems(entity);
        //#else
        return entity.getArmorItems();
        //#endif
    }

    @Pattern
    private static boolean isSuitableFor(Item item, BlockState state) {
        //#if MC >= 12005
        return item.getDefaultStack().isSuitableFor(state);
        //#else
        //$$ return item.isSuitableFor(state);
        //#endif
    }

}
