plugins {
	id( "fabric-loom" ) version "1.+"
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
