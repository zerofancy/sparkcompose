import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.File
import java.io.FileInputStream
import java.util.*

plugins {
    kotlin("jvm") version "1.5.31"
    id("org.jetbrains.compose") version "1.1.0"
}

val versionProperties = Properties().apply {
    load(FileInputStream(File(rootProject.rootDir, "version.properties")))
}

group = "top.ntutn"
version = versionProperties.getProperty("sparkcompose_version")

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://jitpack.io")
    maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies")
}

dependencies {
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation(compose.desktop.currentOs)
    implementation(fileTree(mapOf("dir" to "libs/compile", "include" to listOf("*.jar"))))
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-nop
    implementation("org.slf4j:slf4j-nop:1.7.36")
    implementation ("org.jetbrains.pty4j:pty4j:0.12.7")
//    compileOnly(fileTree("libs/provided"))
//    implementation("com.github.JetBrains.jediterm:jediterm-pty:a433301474")
//    implementation("com.github.JetBrains.jediterm:jediterm-typeahead:a433301474")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            modules("java.sql")
            if (!org.apache.tools.ant.taskdefs.condition.Os.isFamily(org.apache.tools.ant.taskdefs.condition.Os.FAMILY_MAC)) {
                // 有AppImage的target时macOS将无法运行（虽然目标不是macOS，但俺有时会用macOS debug）
                // https://github.com/JetBrains/compose-jb/issues/793
                targetFormats(TargetFormat.AppImage)
            }
            packageName = "SparkCompose"
            packageVersion = project.version.toString()
        }
    }
}

fun getLastBuildNumber(): String {
    val process: Process = Runtime.getRuntime().exec("git rev-parse --short HEAD")
    process.waitFor()
    val reader = BufferedReader(
        InputStreamReader(process.inputStream)
    )
    return reader.readLine().trim()
}

tasks.register("testBuildNumber") {
    doLast {
        println(getLastBuildNumber())
    }
}

// 将appimage打包为deb。不使用官方的打包任务是为了方便控制打包细节。
tasks.register("packageFinalDeb") {
    dependsOn(tasks.getByName("packageAppImage"))
    doLast {
        println("DEB打包开始……")
        val finalDebFile = File(project.buildDir, "finalDeb")
        finalDebFile.deleteRecursively()
        val debianDir = File(project.buildDir, "finalDeb/DEBIAN")
        debianDir.mkdirs()
        val composeDir = File(project.buildDir,"finalDeb/opt/SparkCompose")
        composeDir.mkdirs()
        val sparkDirFile = File(project.buildDir,"compose/binaries/main/app/SparkCompose")
        sparkDirFile.copyRecursively(composeDir)
        File(composeDir, "bin/SparkCompose").setExecutable(true)
        File(project.projectDir, "pkg").copyRecursively(finalDebFile)

        val controlText = """
            Package: top.ntutn.sparkcompose
            Version: ${project.version}
            Maintainer: zerofancy
            Architecture: amd64
            Description: An unofficial spark client. Based on Jetpack Compose.
            Depends: spark-dstore-patch, sh, curl, apt
            
        """.trimIndent() // don't remove the last empty line
        File(debianDir, "control").writeText(controlText)

        println("文件准备完毕，调用dpkg进行打包")
        val targetDebFile = "${project.buildDir}/top.ntutn.sparkcompose_${project.version}_${getLastBuildNumber()}_amd64.deb"
        println("目标文件$targetDebFile")

        val packageCommand = "dpkg -b ${finalDebFile.absolutePath} ${targetDebFile}"
        println("执行命令 $packageCommand")
        val packageProcess = Runtime.getRuntime().exec(packageCommand)
        packageProcess.waitFor()
        var reader = BufferedReader(
            InputStreamReader(packageProcess.inputStream)
        )
        println(reader.readText())
        reader = BufferedReader(
            InputStreamReader(packageProcess.errorStream)
        )
        println(reader.readText())
    }
}