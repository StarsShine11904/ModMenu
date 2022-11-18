package com.enderzombi102.modmenu.util;

import com.enderzombi102.modmenu.api.Badge;

public class BuiltinBadges {
	public static Badge LIBRARY;
	public static Badge CLIENT;
	public static Badge DEPRECATED;
	public static Badge FORGE;
	public static Badge MINECRAFT;

	public static void init() {
		LIBRARY = Badge.register( "modmenu.badge.library", 0xff107454, 0xff093929, "library" );
		CLIENT = Badge.register( "modmenu.badge.clientsideOnly", 0xff2b4b7c, 0xff0e2a55, null );
		DEPRECATED = Badge.register( "modmenu.badge.deprecated", 0xff841426, 0xff530C17, "deprecated" );
		FORGE = Badge.register( "modmenu.badge.forge", 0xff1f2d42, 0xff101721, "forge" );
		MINECRAFT = Badge.register( "modmenu.badge.minecraft", 0xff6f6c6a, 0xff31302f, null );
	}
}
