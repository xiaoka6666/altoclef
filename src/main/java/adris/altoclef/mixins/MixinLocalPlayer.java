package adris.altoclef.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinLocalPlayer extends AbstractClientPlayerEntity {

    public MixinLocalPlayer(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    //#if MC >= 260300
    // 26.x (mojmap): the interpolated pitch/yaw getters (getXRot(float)/getYRot(float)) live
    // on Entity, not LocalPlayer, so they cannot be @Inject targets on LocalPlayer.
    // This mixin's pitch/yaw interception point does not exist on 26.3 - no-op by design.
    //#else
    @Inject(method = "getPitch", at = @At("RETURN"), cancellable = true)
    public void getPitch(float tickDelta, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(super.getPitch(tickDelta));
    }

    @Inject(method = "getYaw", at = @At("RETURN"), cancellable = true)
    public void getYaw(float tickDelta, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(super.getYaw(tickDelta));
    }
    //#endif
}