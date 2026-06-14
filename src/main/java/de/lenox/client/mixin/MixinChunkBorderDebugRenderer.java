package de.lenox.client.mixin;

import de.lenox.client.renderer.ChunkBorderRendererHelper;
import net.minecraft.client.renderer.debug.ChunkBorderRenderer;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(ChunkBorderRenderer.class)
public class MixinChunkBorderDebugRenderer {

    @SuppressWarnings("unused")
    @Inject(method = "emitGizmos", at = @At("HEAD"), cancellable = true)
    private void onEmitGizmos(double cameraX, double cameraY, double cameraZ, DebugValueAccess val, Frustum frustum, float delta, CallbackInfo ci) {
        if (ChunkBorderRendererHelper.onEmitGizmos(cameraY)) {
            ci.cancel();
        }
    }
}
