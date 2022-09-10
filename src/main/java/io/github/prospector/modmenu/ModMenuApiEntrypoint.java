package io.github.prospector.modmenu;

import com.google.common.collect.ImmutableMap;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import io.github.prospector.modmenu.gui.ModMenuOptionsScreen;
import io.github.prospector.modmenu.util.BuiltinBadges;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SettingsScreen;

import java.util.Map;

public class ModMenuApiEntrypoint implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return ModMenuOptionsScreen::new;
	}

	@Override
	public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
		return ImmutableMap.of(
			"minecraft", parent -> new SettingsScreen( parent, MinecraftClient.getInstance().options )
		);
	}

	@Override
	public void onSetupBadges() {
		BuiltinBadges.init();
	}
}
