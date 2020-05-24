package com.github.rpc.modularization.plugin

import com.github.rpc.modularization.plugin.config.ClassModifierExtension
import com.github.rpc.modularization.plugin.config.GlobalConfig
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
        initGlobalConfigFromProperties(project)
        def appExtension = project.extensions.findByType(AppExtension)
        if(appExtension){// app工程才执行字节码修改 模块初始化 和 自动注册
            if(!GlobalConfig.enableInjectTransform){
                return
            }
            project.extensions.create(ClassModifierExtension.EXT_NAME, ClassModifierExtension)
            InjectTransform injectTransform = new InjectTransform(project)
            appExtension.registerTransform(injectTransform)
//            TestInjectTransform testInjectTransform = new TestInjectTransform(project)
//            appExtension.registerTransform(testInjectTransform)
            project.afterEvaluate {
                init(project, injectTransform)
            }
            return
        }

        // library 工程 依赖api
        if(GlobalConfig.enableApiDirSrc){
            ApiDirHelper.apiDir(project)
        }
    }

    static void initGlobalConfigFromProperties(Project project){

        def enableInjectTransform = project.getProperties().get("enableInjectTransform")
        GlobalConfig.enableInjectTransform = enableInjectTransform == 'true'
        LogUtil.d(TAG,"enableInjectTransform ${GlobalConfig.enableInjectTransform}")

        def enableApiDirSrc = project.getProperties().get("enableApiDirSrc")
        GlobalConfig.enableApiDirSrc = enableApiDirSrc == 'true'
        LogUtil.d(TAG,"enableApiDirSrc ${GlobalConfig.enableApiDirSrc}")

        def enableIncrementalCache = project.getProperties().get("enableIncrementalCache")
        GlobalConfig.enableIncrementalCache = enableIncrementalCache == 'true'
        LogUtil.d(TAG,"enableIncrementalCache ${GlobalConfig.enableIncrementalCache}")

    }

    static ClassModifierExtension init(Project project, InjectTransform transformImpl) {
        ClassModifierExtension extension = project.extensions.findByName(ClassModifierExtension.EXT_NAME) as ClassModifierExtension
        extension.project = project
        transformImpl.extension = extension
        LogUtil.d(TAG,"init ${extension.configs}")
        return extension
    }

}