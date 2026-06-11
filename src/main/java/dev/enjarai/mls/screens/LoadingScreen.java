package dev.enjarai.mls.screens;

import com.mojang.math.MatrixUtil;
import dev.enjarai.mls.DrawContextWrapper;
import dev.enjarai.mls.ModerateLoadingScreen;
import dev.enjarai.mls.config.Orientation;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Random;

public abstract class LoadingScreen {
    protected final int patchSize = ModerateLoadingScreen.CONFIG.iconSize();
    protected final Orientation orientation = ModerateLoadingScreen.CONFIG.orientation();
    protected final Minecraft client;
    protected final ArrayList<Identifier> icons;
    protected final Random random = new Random();
    protected final ArrayList<Patch> patches = new ArrayList<>();
    protected double patchTimer = 0f;
    protected boolean tater = ModerateLoadingScreen.CONFIG.showTater();
    protected boolean modsOnlyOnce = ModerateLoadingScreen.CONFIG.modsOnlyOnce();

    public LoadingScreen(Minecraft client) {
        this.client = client;

        icons = ModerateLoadingScreen.compileIconList();
    }

    public abstract void createPatch(Identifier texture);

    protected Identifier getNextTexture() {
        // Summon the holy tater if enabled
        if (tater) {
            tater = false;
            return ModerateLoadingScreen.id("textures/gui/tiny_potato.png");
        }

        return icons.get(random.nextInt(icons.size()));
    }

    public void updatePatches(float delta, boolean ending) {
        processPhysics(delta, ending);

        if (!icons.isEmpty()) {
            patchTimer -= delta;

            if (patchTimer < 0f && !ending) {
                Identifier icon = getNextTexture();

                if (modsOnlyOnce) {
                    icons.remove(icon);
                }
                createPatch(icon);

                patchTimer = getPatchTimer();
            }
        }
    }

    protected double getPatchTimer() {
        return random.nextFloat();
    }

    protected double getOffsetX() {
        return 0;
    }

    protected double getOffsetY() {
        return 0;
    }

    protected int getScreenWidth() {
        return orientation.switchAxes ? client.getWindow().getGuiScaledHeight() : client.getWindow().getScreenWidth();
    }

    protected int getScreenHeight() {
        return orientation.switchAxes ? client.getWindow().getGuiScaledWidth() : client.getWindow().getGuiScaledHeight();
    }

    protected void processPhysics(float delta, boolean ending) {
        for (Patch patch : patches) {
            if (ending)
                patch.fallSpeed *= 1.0 + delta / 3;

            patch.update(delta);
        }
    }

    public void renderPatches(DrawContextWrapper wrapper, float delta, boolean ending, float ticksActive) {
        // spike prevention
        if (delta < 2.0f)
            updatePatches(delta, ending);

        //147 because anything less makes them flash back in the startup screen, anything more makes them dissappear to quickly
        //in the not startup screen
        if (ticksActive >= 200) {
            System.out.println(patches.getFirst().alpha);
            for (Patch patch : patches) {
                //for some reason seems to operate in reverse, use += to make it go down since im lazy
                //nope i fixed it
                patch.alpha -= 5;
            }
        }

        //RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        /*? if <1.21.5*/ /*RenderSystem.enableBlend();*/
        /*? if <1.21.5*/ /*RenderSystem.defaultBlendFunc();*/
        //Likely not needed???? Other mods and minecraft code also just removed it and just did as they did before /shrug

        for (Patch patch : patches) {
            /*? if <1.20*/ /*RenderSystem.setShaderTexture(0, patch.texture);*/
            patch.render(wrapper, getOffsetX(), getOffsetY());
        }
    }

    protected class Patch {
        protected final Identifier texture;
        protected final double horizontal, rotSpeed;
        protected final double scale;
        protected final int patchSize;
        public int alpha = 100;
        public double fallSpeed;
        protected double x, y, rot;

        public Patch(double x, double y, double rot, double horizontal, double fallSpeed, double rotSpeed, double scale, Identifier texture, int patchSize) {
            this.x = x;
            this.y = y;
            this.rot = rot;

            this.horizontal = horizontal;
            this.fallSpeed = fallSpeed;
            this.rotSpeed = rotSpeed;

            this.scale = scale;

            this.texture = texture;

            this.patchSize = patchSize;
        }

        public void update(float delta) {
            x += horizontal * delta;
            y += fallSpeed * delta;

            rot += rotSpeed * delta;

        }

        public void render(DrawContextWrapper wrapper, double offsetX, double offsetY) {
            if (this.alpha < 0) {
                this.alpha = 0;
            }

            Matrix3x2fStack matrices = wrapper.matrices();
            matrices.pushMatrix();
            if (orientation.switchAxes) {
                matrices.translate(
                        (float) perhapsInvert(y + offsetY, getScreenHeight()),
                        (float) perhapsInvert(x + offsetX, getScreenWidth())
                );
            } else {
                matrices.translate(
                        (float) perhapsInvert(x + offsetX, getScreenWidth()),
                        (float) perhapsInvert(y + offsetY, getScreenHeight())
                );
            }

            Matrix4f matrix = new Matrix4f();

            matrix.set(
                    matrices.m00(), matrices.m01(), 0.0F, 0.0F,
                    matrices.m10(), matrices.m11(), 0.0F, 0.0F,
                    0.0F, 0.0F, 1.0F, 0.0F,
                    matrices.m20(), matrices.m21(), 0.0F, 1.0F
            );
            MatrixUtil.mulComponentWise(matrix.rotate((float) rot * 0.017453292F, 0, 0, 1), (float) scale);

            double x1 = -patchSize / 2d;
            double y1 = -patchSize / 2d;
            double x2 = patchSize / 2d;
            double y2 = patchSize / 2d;

            wrapper.drawTexturedQuad(texture, (int) x1, (int) x2, (int) y1, (int) y2, alpha);
            matrices.popMatrix();
        }

        private double perhapsInvert(double value, int fullSize) {
            return orientation.reverseAxes ? fullSize - value : value;
        }
    }
}
