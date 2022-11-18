package com.enderzombi102.modmenu.util;

import com.enderzombi102.modmenu.imixin.ScreenAccessor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;

import java.util.List;

public class Screens {
	public static List<ButtonWidget> getButtons( Screen screen ) {
		return ( (ScreenAccessor) screen ).modmenu$getButtons();
	}
}
