package com.github.rpc.modularization.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.gradle.AppExtension

class ModularizationPlugin implements Plugin<Project> {

    void apply(Project project) {
        println()
        println("=========== ${project.name} ModularizationPlugin start=============")

//        ModuleDebugAbleHelper.doDebugAbleModule(project)
//        // 获取Android扩展
        def android = project.extensions.getByType(AppExtension)
//        // 注册Transform，其实就是添加了Task
//        android.registerTransform(new FirstInjectTransform(project))
        android.registerTransform(new InjectTransform(project))
        project.afterEvaluate {

        }

    }
}