pluginManagement {
	repositories {
		maven( url = "https://maven.fabricmc.net/" )
		gradlePluginPortal()
	}
}

rootProject.name = "modmenu-legacy"

include( "common" )
include( "ver1122" )
include( "ver189" )
