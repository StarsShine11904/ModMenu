package com.enderzombi102.modmenu.api;

import net.minecraft.client.gui.screen.Screen;

@FunctionalInterface
public interface ConfigScreenFactory<S extends Screen> {
	S create( Screen parent );
}
