package dev.enjarai.mls.mixin;


import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/*? if >=1.21.2 {*/
@Mixin(GuiGraphicsExtractor.class)
public interface DrawContextAccessor {
    @Invoker("innerBlit")
    void loadingScreen$drawTexturedQuad(
            RenderPipeline renderPipeline,
            Identifier sprite,
            int x1, int x2, int y1, int y2,
            float u1, float u2, float v1, float v2,
            int color
    );
}
/*?} else if >=1.20 {*//*
@Mixin(net.minecraft.client.gui.DrawContext.class)
public interface DrawContextAccessor {
    @Invoker("drawTexturedQuad")
    void loadingScreen$drawTexturedQuad(
            net.minecraft.util.Identifier identifier,
            int x0,
            int x1,
            int y0,
            int y1,
            int z,
            float u0,
            float u1,
            float v0,
            float v1);
}
*//*?} else {*/
/*@Mixin(net.minecraft.client.gui.DrawableHelper.class)
public interface DrawContextAccessor {
    @Invoker("drawTexturedQuad")
    static void loadingScreen$drawTexturedQuad(
            org.joml.Matrix4f matrix,
            int x0,
            int x1,
            int y0,
            int y1,
            int z,
            float u0,
            float u1,
            float v0,
            float v1) {
        throw new UnsupportedOperationException();
    }
}
*//*?}*/