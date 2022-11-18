package com.enderzombi102.modmenu.api;

import com.enderzombi102.modmenu.util.mod.ModIconHandler;
import net.minecraft.client.texture.NativeImageBackedTexture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Mod {
	@NotNull
	String getId();

	@NotNull
	String getName();

	@NotNull
	NativeImageBackedTexture getIcon( ModIconHandler iconHandler, int i );

	@NotNull
	String getSummary();

	@NotNull
	String getDescription();

	@NotNull
	String getVersion();

	@NotNull
	String getPrefixedVersion();

	@NotNull
	List<String> getAuthors();

	@NotNull
	List<String> getContributors();

	@NotNull
	Set<Badge> getBadges();

	@Nullable
	String getWebsite();

	@Nullable
	String getIssueTracker();

	@Nullable
	String getSource();

	@Nullable
	String getParent();

	@NotNull
	Set<String> getLicense();

	@NotNull
	Map<String, String> getLinks();

	boolean isReal();
}
