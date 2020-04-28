package com.github.rpc.modularization.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.gradle.AppExtension

class ModularizationPlugin implements Plugin<Project> {

    static String TAG = "ModularizationPlugin"

    void apply(Project project) {
        println()
        println("=========== ${project.name} ModularizationPlugin start=============")

//        ModuleDebugAbleHelper.doDebugAbleModule(project)
//        // 获取Android扩展
        project.extensions.create(ClassModifierExtension.EXT_NAME, ClassModifierExtension)

        def android = project.extensions.getByType(AppExtension)
//        // 注册Transform，其实就是添加了Task
//        android.registerTransform(new FirstInjectTransform(project))
        InjectTransform injectTransform = new InjectTransform(project)
        android.registerTransform(injectTransform)
        project.afterEvaluate {
            init(project, injectTransform)
        }

    }

    static ClassModifierExtension init(Project project, InjectTransform transformImpl) {
        ClassModifierExtension extension = project.extensions.findByName(ClassModifierExtension.EXT_NAME) as ClassModifierExtension
        extension.project = project
        transformImpl.extension = extension
        LogUtil.d(TAG,"init ${extension.configs}")
        return extension
    }

}