package com.enderzombi102.modmenu.mixin;

import com.enderzombi102.modmenu.gui.ModsScreen;
import com.enderzombi102.modmenu.gui.widget.ModMenuButtonWidget;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public class MixinGameMenuScreen extends Screen {

	@Inject(method = "init", at = @At("RETURN"))
	private void onInit(CallbackInfo ci) {
		// 1.12.2 暫停選單標準位置：排在「成就/統計」或「選項」按鈕上方/下方
		// 此處將按鈕寬度設為 98（與原版左右雙欄按鈕同寬），放在左下方
		this.method_13411(
			new ModMenuButtonWidget(
				990,
				this.width / 2 - 100,
				this.height / 4 + 72 - 16,
				200,
				20,
				I18n.translate("modmenu.title"),
				button -> this.client.openScreen(new ModsScreen(this))
			)
		);
	}
}
