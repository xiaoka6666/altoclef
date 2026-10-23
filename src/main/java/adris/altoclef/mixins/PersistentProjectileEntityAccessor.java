package adris.altoclef.mixins;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PersistentProjectileEntity.class)
public interface PersistentProjectileEntityAccessor {
    @Accessor("IN_GROUND")
    TrackedData<Boolean> getInGround();
}
