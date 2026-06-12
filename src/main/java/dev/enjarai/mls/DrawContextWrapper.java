package dev.enjarai.mls;

import dev.enjarai.mls.mixin.DrawContextAccessor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2fStack;

/**
 * @param context ? if >=1.21.2 {
 */ // This exists to provide a unified interface for rendering
public record DrawContextWrapper(GuiGraphicsExtractor context) {

    public Matrix3x2fStack matrices() {
        return context.pose();
    }

    public void drawTexturedQuad(Identifier identifier, int x0, int x1, int y0, int y1,
                                 int alpha) {
        ((DrawContextAccessor) context).loadingScreen$drawTexturedQuad(
                RenderPipelines.GUI_TEXTURED, identifier,
                x0, x1, y0, y1,
                0.0f, 1.0f, 0.0f, 1.0f, calculateColor(alpha)
        );
    }

    private int calculateColor(int alpha) {
        if (alpha < 0) alpha = 0;
        if (alpha > 100) alpha = 100;

        int alphaByte = Math.round((alpha / 100.0f) * 255.0f);

        int baseRgb = 0xffffff;

        return (alphaByte << 24) | (baseRgb & 0xffffff);
    }
    /*?} else if >=1.20 {*//*
    private final net.minecraft.client.gui.DrawContext context;
    public DrawContextWrapper(net.minecraft.client.gui.DrawContext context) {
        this.context = context;
    }

    public MatrixStack matrices() {
        return context.getMatrices();
    }

    public void drawTexturedQuad(Identifier identifier, int x0, int x1, int y0, int y1) {
        ((DrawContextAccessor) context).loadingScreen$drawTexturedQuad(
                identifier,
                x0, x1, y0, y1, 0,
                0.0f, 1.0f, 0.0f, 1.0f
        );
    }
    *//*?} else {*/
    /*private final MatrixStack stack;
    public DrawContextWrapper(MatrixStack stack) {
        this.stack = stack;
    }

    public MatrixStack matrices() {
        return stack;
    }
    public void drawTexturedQuad(Identifier identifier, int x0, int x1, int y0, int y1) {
        DrawContextAccessor.loadingScreen$drawTexturedQuad(
                matrices().peek().getPositionMatrix(),
                x0, x1, y0, y1, 0,
                0.0f, 1.0f, 0.0f, 1.0f
        );
    }
    *//*?}*/
}