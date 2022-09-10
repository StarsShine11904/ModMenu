package io.github.prospector.modmenu.util;

import io.github.prospector.modmenu.api.Badge;

import static io.github.prospector.modmenu.api.Badge.register;

public class BuiltinBadges {
	public static Badge LIBRARY;
	public static Badge CLIENT;
	public static Badge DEPRECATED;
	public static Badge FORGE;
	public static Badge MINECRAFT;

	public static void init() {
		LIBRARY = register( "modmenu.badge.library", 0xff107454, 0xff093929, "library" );
		CLIENT = register( "modmenu.badge.clientsideOnly", 0xff2b4b7c, 0xff0e2a55, null );
		DEPRECATED = register( "modmenu.badge.deprecated", 0xff841426, 0xff530C17, "deprecated" );
		FORGE = register( "modmenu.badge.forge", 0xff1f2d42, 0xff101721, "forge" );
		MINECRAFT = register( "modmenu.badge.minecraft", 0xff6f6c6a, 0xff31302f, null );
	}
}
