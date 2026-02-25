package feather.finch.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity; // Изменено
import net.minecraft.server.level.ServerLevel; // Изменено
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {MinecraftServer.class, Entity.class})
public class ExampleMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("FeatherFinch");

    @Inject(at = @At("HEAD"), method = "loadLevel")
    private void featherfinch$onLoadLevel(CallbackInfo info) {
        LOGGER.info("FeatherFinch: Расправляем крылья... Оптимизация RAM!");
        System.gc(); 
        long maxMemory = Runtime.getRuntime().maxMemory() / 1024 / 1024;
        LOGGER.info("FeatherFinch: Доступно памяти: {} MB.", maxMemory);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void featherfinch$smartEntityTick(CallbackInfo ci) {
        if ((Object) this instanceof Entity entity) {
            // В официальных маппингах метод называется level(), а класс - ServerLevel
            if (!entity.level().isClientSide && entity.level() instanceof ServerLevel serverLevel) {
                // Радиус 64 блока
                if (serverLevel.getNearestPlayer(entity.getX(), entity.getY(), entity.getZ(), 64.0, false) == null) {
                    if (entity.level().getGameTime() % 5 != 0) {
                        ci.cancel(); 
                    }
                }
            }
        }
    }
}
