package adris.altoclef.mixins;

import adris.altoclef.eventbus.EventBus;
import adris.altoclef.eventbus.events.BlockPlaceEvent;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public class WorldBlockModifiedMixin {

    @Unique
    private static boolean hasBlock(BlockState state, BlockPos pos) {
        return !state.isAir() && state.isSolidBlock(MinecraftClient.getInstance().world, pos);
    }

    //#if MC >= 260300
    // 26.x (mojmap): the 3-arg block-state-changed callback on Level is
    // updatePOIOnBlockStateChange(BlockPos, BlockState, BlockState). Target it with the
    // explicit mojmap name + remap=false so Mixin resolves it literally (no refmap in the
    // unobfuscated run). Yarn 1.16.5..1.21.11 keep onBlockChanged below.
    @Inject(
            method = "updatePOIOnBlockStateChange", remap = false,
            at = @At("HEAD")
    )
    public void onBlockWasChanged(BlockPos pos, BlockState oldBlock, BlockState newBlock, CallbackInfo ci) {
        if (!hasBlock(oldBlock, pos) && hasBlock(newBlock, pos)) {
            BlockPlaceEvent evt = new BlockPlaceEvent(pos, newBlock);
            EventBus.publish(evt);
        }
    }
    //#else
    //$$ @Inject(
    //$$         method = "onBlockChanged",
    //$$         at = @At("HEAD")
    //$$ )
    //$$ public void onBlockWasChanged(BlockPos pos, BlockState oldBlock, BlockState newBlock, CallbackInfo ci) {
    //$$     if (!hasBlock(oldBlock, pos) && hasBlock(newBlock, pos)) {
    //$$         BlockPlaceEvent evt = new BlockPlaceEvent(pos, newBlock);
    //$$         EventBus.publish(evt);
    //$$     }
    //$$ }
    //#endif

}
