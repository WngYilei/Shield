import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    id("java")
    id("java-library")

    id("maven-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    maven { setUrl("https://maven.aliyun.com/repository/central/") }
    google()
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    implementation("com.android.tools.build:gradle:3.5.2")
    //ASM
    implementation("org.ow2.asm:asm:9.2")
    implementation("org.ow2.asm:asm-util:9.2")
    implementation("org.ow2.asm:asm-commons:9.2")
}


group = "com.xl"
// 描述
description = "一个方法执行防重插件"
// 版本号
version = "1.0.1"


publishing {
    publications {
        create<MavenPublication>("release") {

            groupId = "com.xl" // 请填入你的组件名
            artifactId = "shield" // 请填入你的工件名
            version = "1.0.1" // 请填入工件的版本名

            pom {
                name.set("shield") // (可选)为工件取一个名字
                url.set("https://github.com/WngYilei/Shield") // (可选)网站地址
                developers {
                    developer {
                        name.set("WangYilei") // (可选)开发者名称
                        email.set("wangyilei0318@gmail.com") // (可选)开发者邮箱
                    }
                }
            }
        }
    }
    repositories {
        maven {
            group = "com.xl"
            version = "1.0.1"
            setUrl("../repo/")
        }
    }
}
