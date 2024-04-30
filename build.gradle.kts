plugins {
	id("fabric-loom") version "1.6-SNAPSHOT"
	id("maven-publish")
}

base {
	archivesName = project.property("archives_base_name").toString()
	version = project.property("mod_version").toString()
	group = project.property("maven_group").toString()
}

repositories {
	// Add repositories to retrieve artifacts from in here.
	// You should only use this when depending on other mods because
	// Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
	// See https://docs.gradle.org/current/userguide/declaring_repositories.html
	// for more information about repositories.
}

dependencies {
	// To change the versions see the gradle.properties file
	minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
	mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
	modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")

	// Fabric API. This is technically optional, but you probably want it anyway.
	modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")
}

tasks {
	processResources {
		inputs.property("version", project.version)

		filesMatching("fabric.mod.json") {
			expand(mapOf("version" to project.version)) {
				escapeBackslash = true
			}
		}
	}

	jar {
		from("LICENSE") {
			rename { "${it}_${project.base.archivesName.get()}"}
		}
	}

	withType<JavaCompile> {
		options.encoding = "UTF-8"
		options.release = 21
	}

	javadoc {
		// disables annoying javadoc warnings, remove this if you care about those
		(options as CoreJavadocOptions).addBooleanOption("Xdoclint:none", true)
	}

	java {
		withSourcesJar()
		withJavadocJar()

		toolchain {
			languageVersion = JavaLanguageVersion.of(21)
		}

		sourceCompatibility = JavaVersion.VERSION_21
		targetCompatibility = JavaVersion.VERSION_21
	}
}

// configure the maven publication
publishing {
	publications {
		register("mavenJava", MavenPublication::class) {
			from(components["java"])
		}
	}

	// See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
	repositories {
		// Add repositories to publish to here.
		// Notice: This block does NOT have the same function as the block in the top level.
		// The repositories here will be used for publishing your artifact, not for
		// retrieving dependencies.
	}
}
