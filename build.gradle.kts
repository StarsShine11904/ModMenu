plugins {
	id( "com.modrinth.minotaur" ) version "2.+"
	id( "fabric-loom" ) version "1.+"
	`maven-publish`
}

val minecraft_version: String by project
val mappings: String by project
val loader: String by project
val fabric_version: String by project
val guava_version: String by project

version = "$version+$minecraft_version"

repositories {
	maven( url = "https://jitpack.io" )
	maven( url = "https://maven.legacyfabric.net" )
}

loom {
	intermediaryUrl.set( "https://maven.legacyfabric.net/net/fabricmc/intermediary/%\$s/intermediary-%1\$s-v2.jar" )
}

dependencies {
	minecraft( "com.mojang:minecraft:$minecraft_version" )
	mappings( "net.legacyfabric:yarn:$minecraft_version+build.$mappings:v2" )
	modImplementation( "net.fabricmc:fabric-loader:$loader" )
	modImplementation( "net.legacyfabric.legacy-fabric-api:legacy-fabric-resource-loader-v1:$fabric_version" )

	//noinspection VulnerableLibrariesLocal,GradlePackageUpdate
	implementation( "com.google.guava:guava:$guava_version" )
}

tasks.withType<ProcessResources> {
	inputs.property( "version", version )
	inputs.property( "minecraft_version", minecraft_version )

	filesMatching("fabric.mod.json") {
		expand(
			"version" to version,
			"minecraft_version" to minecraft_version,
		)
	}
}

tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
	sourceCompatibility = "8"
	options.release.set(8)
}

// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
// if it is present.
// If you remove this task, sources will not be generated.
val sourcesJar by tasks.registering(Jar::class) {
	dependsOn( tasks.classes )
	archiveClassifier.set( "sources" )
	from( sourceSets.main.get().allSource )
}

tasks.withType<Jar> {
	from( "LICENSE" )
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			// add all the jars that should be included when publishing to maven
			artifact(tasks["remapJar"]) {
				builtBy( tasks["remapJar"] )
			}
			artifact(sourcesJar) {
				builtBy( tasks["remapSourcesJar"] )
			}
		}
	}

	// select the repositories you want to publish to
	repositories {
		maven {
			name = "LegacyFabric"
			credentials(PasswordCredentials::class.java)
			url = uri( "https://maven.legacyfabric.net/" )
		}
		maven {
			name = "Repsy"
			credentials(PasswordCredentials::class.java)
			url = uri("https://repsy.io/mvn/enderzombi102/mc")
		}
	}
}

modrinth {
	token.set( if ( hasProperty("modrinth_token") ) project.ext["modrinth_token"] as String else "" )
	projectId.set( "XzTYkVLx" )
	versionNumber.set( version as String ) // You don"t need to set this manually. Will fail if Modrinth has this version already
	versionName.set( "Legacy Mod Menu $version" )
	versionType.set( "release" ) // This is the default
	uploadFile.set( tasks["remapJar"] ) // With Fabric Loom or Architectury Loom, this MUST be set to `remapJar` instead of `jar`!
	gameVersions.set( listOf( minecraft_version ) ) // Must be an array, even with only one version
	dependencies.set( listOf() )
}
