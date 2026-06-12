package dev.enjarai.mls.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.enjarai.mls.DrawContextWrapper;
import dev.enjarai.mls.ModerateLoadingScreen;
import dev.enjarai.mls.screens.LoadingScreen;
import dev.enjarai.mls.screens.SnowFlakesScreen;
import dev.enjarai.mls.screens.StackingScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(LoadingOverlay.class)
public abstract class SplashOverlayMixin extends Overlay {
    @Final
    @Shadow
    private Minecraft minecraft;
    @Unique
    private LoadingScreen moderateLoadingScreen$loadingScreen;

    private long now = Util.getMillis();

    @Shadow
    private long fadeOutStart;

    private float ticksActive = 0;

    @Shadow
    private static int replaceAlpha(int color, int alpha) {
        throw new UnsupportedOperationException("Shadowed method somehow called outside mixin. Exorcise your computer.");
    }

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void moderateLoadingScreen$constructor(Minecraft minecraft, ReloadInstance reload, Consumer onFinish, boolean fadeIn, CallbackInfo ci) {
        moderateLoadingScreen$loadingScreen = switch (ModerateLoadingScreen.CONFIG.screenType()) {
            case SNOWFLAKES -> new SnowFlakesScreen(this.minecraft);
            case STACKING -> new StackingScreen(this.minecraft);
        };
    }

    /*? if >=1.21.2 {*/
    // Replace the color used for the background fill of the splash screen
    @ModifyArg(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V"), index = 1)
    private int moderateLoadingScreen$changeColor(int in) {
        if (this.minecraft.options.darkMojangStudiosBackground().get())
            return in;
        return replaceAlpha(ModerateLoadingScreen.CONFIG.backgroundColor().rgb(), in >> 24); // Use existing transparency
    }

    // For some reason Mojang decided to not use `fill` in a specific case, so we have to replace a local variable
    @ModifyVariable(method = "extractRenderState", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/function/IntSupplier;getAsInt()I"), name = "alpha")
    private int moderateLoadingScreen$changeColorGl(int in) {
        return this.minecraft.options.darkMojangStudiosBackground().get() ? in : ModerateLoadingScreen.CONFIG.backgroundColor().rgb();
    }

    // Render before third getWindow to render before the logo
    @Inject(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;guiWidth()I", ordinal = 2))
    private void moderateLoadingScreen$renderPatches(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        ticksActive++;
        moderateLoadingScreen$loadingScreen.renderPatches(new DrawContextWrapper(graphics), a, a >= 1.0f, ticksActive);
    }

    // Modify logo transparency if needed, multiplies with the original to ensure transitions work normally
    @ModifyArg(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ARGB;white(F)I"), index = 0)
    private float moderateLoadingScreen$modifyLogoTransparency(float original) {
        return original * ModerateLoadingScreen.CONFIG.logoOpacity() / 100f;
    }

    // Modify loading bar transparency if needed, again multiplying with the original
    @ModifyArg(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/LoadingOverlay;extractProgressBar(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIIIF)V"), index = 5)
    private float moderateLoadingScreen$modifyBarTransparency(float original) {
        return original * ModerateLoadingScreen.CONFIG.barOpacity() / 100f;
    }

    /*?} else if >=1.20.1 {*//*
    // Replace the color used for the background fill of the splash screen
    @ModifyArg(method = "render(Lnet/minecraft/minecraft/gui/DrawContext;IIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/minecraft/gui/DrawContext;fill(Lnet/minecraft/minecraft/render/RenderLayer;IIIII)V"), index = 5)
    private int moderateLoadingScreen$changeColor(int in) {
        if (this.minecraft.options.getMonochromeLogo().getValue())
            return in;
        return replaceAlpha(ModerateLoadingScreen.CONFIG.backgroundColor().rgb(), in >> 24); // Use existing transparency
    }

    // For some reason Mojang decided to not use `fill` in a specific case, so we have to replace a local variable
    @ModifyVariable(method = "render(Lnet/minecraft/minecraft/gui/DrawContext;IIF)V", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/function/IntSupplier;getAsInt()I", ordinal = 2), ordinal = 4)
    private int moderateLoadingScreen$changeColorGl(int in) {
        return this.minecraft.options.getMonochromeLogo().getValue() ? in : ModerateLoadingScreen.CONFIG.backgroundColor().rgb();
    }

    // Render before third getWindow to render before the logo
    @Inject(method = "render(Lnet/minecraft/minecraft/gui/DrawContext;IIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/minecraft/gui/DrawContext;getScaledWindowWidth()I", ordinal = 2), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void moderateLoadingScreen$renderPatches(net.minecraft.minecraft.gui.DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci, int i, int j, long l, float f) {
        moderateLoadingScreen$loadingScreen.renderPatches(new DrawContextWrapper(context), delta, f >= 1.0f);
    }

    // Modify logo transparency if needed, multiplies with the original to ensure transitions work normally
    @ModifyArg(method = "render(Lnet/minecraft/minecraft/gui/DrawContext;IIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/minecraft/gui/DrawContext;setShaderColor(FFFF)V"), index = 3)
    private float moderateLoadingScreen$modifyLogoTransparency(float original) {
        return original * ModerateLoadingScreen.CONFIG.logoOpacity() / 100f;
    }

    // Reset RenderSystem shader color to prevent rendering everything else with the modified transparency
    @Inject(method = "render(Lnet/minecraft/minecraft/gui/DrawContext;IIF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;defaultBlendFunc()V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void moderateLoadingScreen$resetTransparency(net.minecraft.minecraft.gui.DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci,
                                                         int i, int j, long l, float f, float g, float h) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, h);
    }

    // Modify loading bar transparency if needed, again multiplying with the original
    @ModifyArg(method = "render(Lnet/minecraft/minecraft/gui/DrawContext;IIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/minecraft/gui/screen/LoadingOverlay;renderProgressBar(Lnet/minecraft/minecraft/gui/DrawContext;IIIIF)V"), index = 5)
    private float moderateLoadingScreen$modifyBarTransparency(float original) {
        return original * ModerateLoadingScreen.CONFIG.barOpacity() / 100f;
    }
    *//*?} else {*/
    /*// Replace the color used for the background fill of the splash screen
    @ModifyArg(
            method = "render(Lnet/minecraft/minecraft/util/math/MatrixStack;IIF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/minecraft/gui/screen/LoadingOverlay;fill(Lnet/minecraft/minecraft/util/math/MatrixStack;IIIII)V"),
            index = 5
    )
    private int moderateLoadingScreen$changeColor(int in) {
        if (this.minecraft.options.getMonochromeLogo().getValue())
            return in;

        return replaceAlpha(ModerateLoadingScreen.CONFIG.backgroundColor().rgb(), in >> 24); // Use existing transparency
    }

    // For some reason Mojang decided to not use `fill` in a specific case, so we have to replace a local variable
    @ModifyVariable(
            method = "render(Lnet/minecraft/minecraft/util/math/MatrixStack;IIF)V",
            at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/function/IntSupplier;getAsInt()I", ordinal = 2),
            ordinal = 4 // int m (or int o according to mixin apparently)
    )
    private int moderateLoadingScreen$changeColorGl(int in) {
        return this.minecraft.options.getMonochromeLogo().getValue() ? in : ModerateLoadingScreen.CONFIG.backgroundColor().rgb();
    }

    // Render before third getWindow to render before the logo
    @Inject(
            method = "render(Lnet/minecraft/minecraft/util/math/MatrixStack;IIF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/minecraft/MinecraftClient;getWindow()Lnet/minecraft/minecraft/util/Window;", ordinal = 2),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void moderateLoadingScreen$renderPatches(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci,
                                                     int i, int j, long l, float f) {
        moderateLoadingScreen$loadingScreen.renderPatches(new DrawContextWrapper(matrices), delta, f >= 1.0f);
    }

    // Modify logo transparency if needed, multiplies with the original to ensure transitions work normally
    @ModifyArg(
            method = "render(Lnet/minecraft/minecraft/util/math/MatrixStack;IIF)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V"),
            index = 3
    )
    private float moderateLoadingScreen$modifyLogoTransparency(float original) {
        return original * ModerateLoadingScreen.CONFIG.logoOpacity() / 100f;
    }

    // Reset RenderSystem shader color to prevent rendering everything else with the modified transparency
    @Inject(
            method = "render(Lnet/minecraft/minecraft/util/math/MatrixStack;IIF)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;defaultBlendFunc()V")
    )
    private void moderateLoadingScreen$resetTransparency(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci, @Local(ordinal = 3) float h) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, h);
    }

    // Modify loading bar transparency if needed, again multiplying with the original
    @ModifyArg(
            method = "render(Lnet/minecraft/minecraft/util/math/MatrixStack;IIF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/minecraft/gui/screen/LoadingOverlay;renderProgressBar(Lnet/minecraft/minecraft/util/math/MatrixStack;IIIIF)V"),
            index = 5
    )
    private float moderateLoadingScreen$modifyBarTransparency(float original) {
        return original * ModerateLoadingScreen.CONFIG.barOpacity() / 100f;
    }
    *//*?}*/
}
