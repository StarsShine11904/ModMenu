@file:Suppress("UnstableApiUsage")
plugins {
	id( "com.github.johnrengelman.shadow") version "7.1.2" apply false
	id( "com.modrinth.minotaur" ) version "2.+" apply false
	id( "org.quiltmc.loom" ) version "1.+" apply false
	`maven-publish`
	java
}

val rootVersion = version
fun libs() = extensions.getByType(VersionCatalogsExtension::class).named("libs")
val common = project(":common")
subprojects {
	apply( plugin = "com.github.johnrengelman.shadow" )
	apply( plugin = "com.modrinth.minotaur" )
	apply( plugin = "org.quiltmc.loom" )
	apply( plugin = "maven-publish" )

	val minecraftVersion: String by project
	val mappings: String by project
	val fabricVersion: String by project

	version = "$rootVersion+$minecraftVersion"

	repositories {
		maven( url = "https://jitpack.io" )
		maven( url = "https://maven.legacyfabric.net" )
	}

	extensions.getByType<net.fabricmc.loom.api.LoomGradleExtensionAPI>().run {
		intermediaryUrl.set( "https://maven.legacyfabric.net/net/legacyfabric/intermediary/%1\$s/intermediary-%1\$s-v2.jar" )

		if ( project != common ) {
			runConfigs["client"].run {
				runDir = "../run"
				isIdeConfigGenerated = true
			}
			runConfigs["server"].run {
				runDir = "../run"
				isIdeConfigGenerated = true
			}
		}
	}

	dependencies {
		"minecraft"( "com.mojang:minecraft:$minecraftVersion" )
		"mappings"( "net.legacyfabric:yarn:$minecraftVersion+build.$mappings:v2" )
		"modImplementation"( libs().findLibrary("loader").get() )
		"modImplementation"( "net.legacyfabric.legacy-fabric-api:legacy-fabric-resource-loader-v1:$fabricVersion" )

		//noinspection VulnerableLibrariesLocal,GradlePackageUpdate
		implementation( libs().findLibrary("guava").get() )
		"include"( libs().findLibrary("enderlib").get() )

		if ( project != common )
			implementation( project(":common") )
	}

	java.toolchain.languageVersion.set( JavaLanguageVersion.of( 8 ) )
	java.withJavadocJar()
	java.withSourcesJar()

	tasks.withType<JavaCompile> {
		options.encoding = "UTF-8"
		sourceCompatibility = "8"
		options.release.set(8)
	}

	tasks.withType<ProcessResources> {
		inputs.property( "version", version )
		filteringCharset = "UTF-8"

		filesMatching("fabric.mod.json") {
			expand( "version" to version )
		}
	}

	if ( project != common ) {
		val shadowedRemappedJar by tasks.registering(com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class) {
			configurations = listOf()
			archiveClassifier.set( "" )

			dependsOn( tasks["remapJar"], common.tasks["remapJar"] )
			from( tasks["remapJar"].outputs )
			from( common.tasks["remapJar"].outputs )
			from("LICENSE") {
				rename { "${it}_$archiveBaseName" }
			}
		}

		val shadowedRemappedSourcesJar by tasks.registering(com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class) {
			configurations = listOf()
			archiveClassifier.set( "sources" )

			dependsOn( tasks["remapSourcesJar"], common.tasks["remapSourcesJar"] )
			from( tasks["remapSourcesJar"].outputs )
			from( common.tasks["remapSourcesJar"].outputs )
			from("LICENSE") {
				rename { "${it}_$archiveBaseName" }
			}
		}

		publishing {
			publications {
				create<MavenPublication>("mavenJava") {
					// add all the jars that should be included when publishing to maven
					artifact( shadowedRemappedJar )
					artifact( tasks["sourcesJar"] ) {
						builtBy( shadowedRemappedSourcesJar )
					}
				}
			}

			// select the repositories you want to publish to
			repositories {
				maven {
					name = "LegacyFabric"
					credentials(PasswordCredentials::class.java)
					url = uri("https://maven.legacyfabric.net/")
				}
				maven {
					name = "Repsy"
					credentials(PasswordCredentials::class.java)
					url = uri("https://repsy.io/mvn/enderzombi102/mc")
				}
			}
		}

		extensions.getByType<com.modrinth.minotaur.ModrinthExtension>().run {
			token.set(if (hasProperty("modrinth_token")) project.ext["modrinth_token"] as String else "")
			projectId.set("XzTYkVLx")
			versionNumber.set(version as String) // You don"t need to set this manually. Will fail if Modrinth has this version already
			versionName.set("Legacy Mod Menu $version")
			versionType.set("release") // This is the default
			uploadFile.set( shadowedRemappedJar as Any ) // With Fabric Loom or Architectury Loom, this MUST be set to `remapJar` instead of `jar`!
			gameVersions.set(listOf(minecraftVersion)) // Must be an array, even with only one version
			dependencies.set(listOf())
		}
	}
}
