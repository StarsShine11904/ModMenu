package com.enderzombi102.modmenu.api;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public final class Badge {
	private static final Map<String, Badge> BADGES = new HashMap<>();

	private final Text text;
	private final int outlineColor, fillColor;

	private Badge( String translationKey, int outlineColor, int fillColor ) {
		this.text = new TranslatableText( translationKey );
		this.outlineColor = outlineColor;
		this.fillColor = fillColor;
	}

	public Text getText() {
		return this.text;
	}

	public int getOutlineColor() {
		return this.outlineColor;
	}

	public int getFillColor() {
		return this.fillColor;
	}

	public static @Nullable Badge get( @NotNull String key ) {
		return BADGES.get( key );
	}

	public static Set<Badge> of( String... badgeKeys ) {
		return Arrays.stream( badgeKeys )
			.map( BADGES::get )
			.filter( Objects::nonNull )
			.collect( Collectors.toSet() );
	}

	public static Set<Badge> convert( Set<String> badgeKeys ) {
		return badgeKeys.stream()
			.map( BADGES::get )
			.filter( Objects::nonNull )
			.collect( Collectors.toSet() );
	}

	public static @NotNull Badge register( @NotNull String translationKey, int outlineColor, int fillColor, @Nullable String key ) {
		Badge badge = new Badge( translationKey, outlineColor, fillColor );
		if ( key != null )
			BADGES.putIfAbsent( key, badge );
		return badge;
	}
}
