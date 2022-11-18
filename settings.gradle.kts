pluginManagement {
	repositories {
		maven( url = "https://maven.fabricmc.net" )
		maven( url = "https://maven.quiltmc.org/repository/release" )
		maven( url = "https://maven.quiltmc.org/repository/snapshots" )
		gradlePluginPortal()
	}
}

rootProject.name = "modmenu-legacy"

include( "common" )
include( "ver1122" )
include( "ver189" )
