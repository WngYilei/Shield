package com.xl.buildsrc

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project


/**
 * @Author : wyl
 * @Date : 2023/4/20
 * Desc :
 */
class ShieldPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val appExtension: AppExtension? = target.extensions.findByType(AppExtension::class.java)
        appExtension?.registerTransform(ShieldTransform())
    }
}