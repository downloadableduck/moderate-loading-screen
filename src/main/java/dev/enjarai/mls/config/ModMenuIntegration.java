package dev.enjarai.mls.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import static dev.enjarai.mls.ModerateLoadingScreen.CONFIG;
import static dev.enjarai.mls.ModerateLoadingScreen.id;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {

        return (screen) -> new ModConfigScreen(id("config"), CONFIG, null);
    }
}
