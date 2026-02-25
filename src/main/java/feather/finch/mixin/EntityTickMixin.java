package feather.finch.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityTickMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void featherfinch$smartTick(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        
        // Проверяем: мы на сервере и это не игрок
        if (!entity.level().isClientSide() && entity.level() instanceof ServerLevel serverLevel) {
            // Если рядом нет игроков в радиусе 64 блоков
            if (serverLevel.getNearestPlayer(entity.getX(), entity.getY(), entity.getZ(), 64.0, false) == null) {
                // Пропускаем 4 тика из 5 (экономия 80% CPU на мобах вдали)
                if (entity.level().getGameTime() % 5 != 0) {
                    ci.cancel();
                }
            }
        }
    }
}
