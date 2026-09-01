package com.enderzombi102.modmenu.util.mod;

import com.enderzombi102.modmenu.ModMenu;
import com.enderzombi102.modmenu.api.Mod;
import com.enderzombi102.modmenu.util.BuiltinBadges;
import com.enderzombi102.modmenu.gui.ModsScreen;
import net.minecraft.client.resource.language.I18n;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ModSearch {

	public static boolean validSearchQuery( String query ) {
		return query != null && !query.isEmpty();
	}

	public static List<Mod> search( ModsScreen screen, String query, List<Mod> candidates ) {
		if ( !validSearchQuery( query ) )
			return candidates;

		return candidates.stream()
			.filter( modContainer -> passesFilters( screen, modContainer, query.toLowerCase( Locale.ROOT ) ) )
			.collect( Collectors.toList() );
	}

	private static boolean passesFilters( ModsScreen screen, Mod mod, String query ) {
		String modId = mod.getId();
		String modDescription = mod.getDescription();
		String modSummary = mod.getSummary();

		// 使用 I18n.translate 取得當前語言設定下的搜尋關鍵詞
		String library = I18n.translate( "modmenu.searchTerms.library" ).toLowerCase( Locale.ROOT );
		String deprecated = I18n.translate( "modmenu.searchTerms.deprecated" ).toLowerCase( Locale.ROOT );
		String clientside = I18n.translate( "modmenu.searchTerms.clientside" ).toLowerCase( Locale.ROOT );
		String configurable = I18n.translate( "modmenu.searchTerms.configurable" ).toLowerCase( Locale.ROOT );

		// 比對模組名稱、ID、描述、作者及各項徽章標籤
		if (
			mod.getName().toLowerCase( Locale.ROOT ).contains( query )
				|| modId.toLowerCase( Locale.ROOT ).contains( query )
				|| modDescription.toLowerCase( Locale.ROOT ).contains( query )
				|| modSummary.toLowerCase( Locale.ROOT ).contains( query )
				|| authorMatches( mod, query )
				|| ( library.contains( query ) && mod.getBadges().contains( BuiltinBadges.LIBRARY ) )
				|| ( deprecated.contains( query ) && mod.getBadges().contains( BuiltinBadges.DEPRECATED ) )
				|| ( clientside.contains( query ) && mod.getBadges().contains( BuiltinBadges.CLIENT ) )
				|| ( configurable.contains( query ) && Boolean.TRUE.equals( screen.getModHasConfigScreen().get( modId ) ) )
		) return true;

		// 若子模組符合條件，父模組也顯示
		if ( ModMenu.PARENT_MAP.containsKey( mod ) ) {
			for ( Mod child : ModMenu.PARENT_MAP.get( mod ) ) {
				if ( passesFilters( screen, child, query ) )
					return true;
			}
		}
		return false;
	}

	private static boolean authorMatches( Mod mod, String query ) {
		return mod.getAuthors().stream()
			.map( s -> s.toLowerCase( Locale.ROOT ) )
			.anyMatch( s -> s.contains( query.toLowerCase( Locale.ROOT ) ) );
	}
}
