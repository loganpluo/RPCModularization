package com.github.rpc.modularization.plugin

import com.github.rpc.modularization.plugin.config.ClassModifierExtension
import com.github.rpc.modularization.plugin.util.LogUtil
import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.gradle.AppExtension

class ModularizationPlugin implements Plugin<Project> {

    static String TAG = "ModularizationPlugin"

    void apply(Project project) {
        println()
        println("=========== ${project.name} ModularizationPlugin start=============")

//        ModuleDebugAbleHelper.doDebugAbleModule(project)
        def appExtension = project.extensions.findByType(AppExtension)
        if(appExtension){// app工程才执行字节码修改 模块初始化 和 自动注册
            project.extensions.create(ClassModifierExtension.EXT_NAME, ClassModifierExtension)
//            def android = project.extensions.getByType(AppExtension)
            InjectTransform injectTransform = new InjectTransform(project)
            appExtension.registerTransform(injectTransform)
            project.afterEvaluate {
                init(project, injectTransform)
            }
        }else{// library 工程 依赖api
            ApiDirHelper.apiDir(project)
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