package dev.enjarai.mls;

import com.mojang.blaze3d.platform.NativeImage;
import dev.enjarai.mls.config.ModConfig;
import dev.enjarai.mls.config.ModConfigScreen;
import io.wispforest.owo.config.ui.ConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.Validate;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

public class ModerateLoadingScreen implements ClientModInitializer {
    public static final String MODID = "moderate-loading-screen";
    public static final ModConfig CONFIG = ModConfig.createAndLoad();

    // Construct list of mod icons, main principles copied from mod menu
    public static ArrayList<Identifier> compileIconList() {
        ArrayList<String> blacklistRegex = new ArrayList<>();
        for (String i : CONFIG.modIdBlacklist()) {
            blacklistRegex.add(createRegexFromGlob(i));
        }

        ArrayList<Identifier> result = new ArrayList<>();
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            ModMetadata metadata = mod.getMetadata();

            String path = metadata.getIconPath(128).orElse("assets/" + metadata.getId() + "/icon.png");
            DynamicTexture texture = getIconTexture(mod, path);

            // Ignore blacklisted mods
            for (String i : blacklistRegex) {
                if (metadata.getId().matches(i)) {
                    texture = null;
                    break;
                }
            }

            // Ignore libraries if that option is enabled
            if (CONFIG.hideLibraries()) {
                try {
                    CustomValue modObj = metadata.getCustomValue("modmenu");
                    if (modObj != null && modObj.getAsObject().containsKey("badges")) {
                        for (CustomValue badge : modObj.getAsObject().get("badges").getAsArray()) {
                            if (Objects.equals(badge.getAsString(), "library")) {
                                texture = null;
                                break;
                            }
                        }
                    }
                } catch (Throwable ignored) {
                }
            }

            if (texture != null) {
                Identifier iconLocation = id(metadata.getId() + "_icon");

                Minecraft.getInstance().getTextureManager().register(iconLocation, texture);
                result.add(iconLocation);
            }
        }

        return result;
    }

    private static DynamicTexture getIconTexture(ModContainer iconSource, String iconPath) {
        try {
            Path path = iconSource.findPath(iconPath).get();
            try (InputStream inputStream = Files.newInputStream(path)) {
                NativeImage image = NativeImage.read(Objects.requireNonNull(inputStream));
                Validate.validState(image.getHeight() == image.getWidth(), "Must be square icon");
                return new DynamicTexture(/*? if >1.21.4 {*/ () -> iconSource.getMetadata().getName() /*?}*/, image);
            }

        } catch (Throwable t) {
            return null;
        }
    }

    // https://stackoverflow.com/questions/45321050/java-string-matching-with-wildcards
    private static String createRegexFromGlob(String glob) {
        StringBuilder out = new StringBuilder("^");
        for (int i = 0; i < glob.length(); ++i) {
            final char c = glob.charAt(i);
            switch (c) {
                case '*' -> out.append(".*");
                case '?' -> out.append('.');
                case '.' -> out.append("\\.");
                case '\\' -> out.append("\\\\");
                default -> out.append(c);
            }
        }
        out.append('$');
        return out.toString();
    }

    public static Identifier id(String path) {
        /*? if >=1.21 {*/
        return Identifier.fromNamespaceAndPath(MODID, path);
        /*?} else {*//*
        return new Identifier(MODID, path);
        *//*?} */
    }

    @Override
    public void onInitializeClient() {
        //ClientLifecycleEvents.CLIENT_STARTED.register((_) -> {
            //ConfigScreen.create(CONFIG, new ModConfigScreen(id("config"), CONFIG, null));
        //});
    }
}
