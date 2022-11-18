package com.enderzombi102.modmenu;

import com.enderzombi102.modmenu.api.ConfigScreenFactory;
import com.enderzombi102.modmenu.api.ModMenuApi;
import com.enderzombi102.modmenu.util.BuiltinBadges;
import com.google.common.collect.ImmutableMap;
import com.enderzombi102.modmenu.gui.ModMenuOptionsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SettingsScreen;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ModMenuApiEntrypoint implements ModMenuApi {

	@Override
	public @NotNull ConfigScreenFactory<?> getModConfigScreenFactory() {
		return ModMenuOptionsScreen::new;
	}

	@Override
	public @NotNull Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
		return ImmutableMap.of(
			"minecraft", parent -> new SettingsScreen( parent, MinecraftClient.getInstance().options )
		);
	}

	@Override
	public void onSetupBadges() {
		BuiltinBadges.init();
	}
}
