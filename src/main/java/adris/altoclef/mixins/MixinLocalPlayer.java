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
    // 26.x (mojmap): LocalPlayer no longer declares interpolated pitch/yaw getters; they
    // live on Entity as getXRot(float)/getYRot(float). Target the inherited 1-arg getters
    // literally (remap=false) so Mixin resolves mojmap names without a refmap.
    @Inject(method = "getXRot", remap = false, at = @At("RETURN"), cancellable = true)
    public void onPitch(float tickDelta, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(super.getXRot(tickDelta));
    }

    @Inject(method = "getYRot", remap = false, at = @At("RETURN"), cancellable = true)
    public void onYaw(float tickDelta, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(super.getYRot(tickDelta));
    }
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