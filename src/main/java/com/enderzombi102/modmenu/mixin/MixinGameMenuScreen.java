package com.enderzombi102.modmenu.mixin;

import com.enderzombi102.modmenu.gui.ModsScreen;
import com.enderzombi102.modmenu.gui.widget.ModMenuButtonWidget;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class MixinGameMenuScreen extends Screen {

	@Inject(method = "init", at = @At("RETURN"))
	private void onInit(CallbackInfo ci) {
		this.method_13411(
			new ModMenuButtonWidget(
					990,
					this.width / 2 - 100,
					this.height / 4 + 72 - 16,
					200,
					20,
					new LiteralText( I18n.translate( "modmenu.title" ) ),
					this
				)
			);
	}
}
