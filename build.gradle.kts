import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.BufferedReader
import java.io.InputStreamReader

plugins {
    kotlin("jvm") version "1.5.31"
    id("org.jetbrains.compose") version "1.0.0"
}

group = "top.ntutn"
version = "0.0.1"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation(compose.desktop.currentOs)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            modules("java.sql")
            targetFormats(TargetFormat.AppImage)
            packageName = "SparkCompose"
            packageVersion = "1.0.0"
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
            Depends: spark-dstore-patch
            
        """.trimIndent() // don't remove the last empty line
        File(debianDir, "control").writeText(controlText)

        println("文件准备完毕，调用dpkg进行打包")
        val targetDebFile = "${project.buildDir}/top.ntutn.sparkcompose_${project.version}_${getLastBuildNumber()}_amd64.deb"
        println("目标文件$targetDebFile")

        val packageCommand = "dpkg -b ${finalDebFile.absolutePath} ${project.buildDir.absolutePath}"
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