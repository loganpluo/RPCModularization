package com.github.rpc.modularization.plugin

import org.gradle.api.Project
import org.gradle.util.GradleVersion

import java.util.regex.Pattern

class ModuleDebugAbleHelper {

    static final String DEBUG_DIR = "src/main/debug/"
    static final String MODULE_MAIN_APP = "mainApp"

    static boolean taskIsAssemble
    static String mainModuleName

    static boolean doDebugAbleModule(Project project) {
        taskIsAssemble = false
        mainModuleName = null
        initByTask(project)

        if(isMainApp(project)){//主app工程一直以application运行
            project.apply plugin: 'com.android.application'
        }else{

            //处理library debug独立运行
            boolean isDebugAlone = Boolean.parseBoolean((project.properties.get("isDebugAlone")))
            println "doDebugAbleModule isDebugAlone=${isDebugAlone}"
            if (isDebugAlone) {//模块可以单独run
                project.apply plugin: 'com.android.application'
                println "project.android.sourceSets.main"
                project.android.sourceSets.main {
                    //run模式下，如果存在src/main/debug/AndroidManifest.xml，则自动使用其作为manifest文件
                    def debugManifest = "${DEBUG_DIR}AndroidManifest.xml"
                    println "debugManifest"
                    if (project.file(debugManifest).exists()) {
                        manifest.srcFile debugManifest
                    }
                    //debug模式下，如果存在src/main/debug/assets，则自动将其添加到assets源码目录
                    if (project.file("${DEBUG_DIR}assets").exists()) {
                        assets.srcDirs = ['src/main/assets', "${DEBUG_DIR}assets"]
                    }
                    //debug模式下，如果存在src/main/debug/java，则自动将其添加到java源码目录
                    if (project.file("${DEBUG_DIR}java").exists()) {
                        java.srcDirs = ['src/main/java', "${DEBUG_DIR}java"]
                    }
                    //debug模式下，如果存在src/main/debug/res，则自动将其添加到资源目录
                    if (project.file("${DEBUG_DIR}res").exists()) {
                        res.srcDirs = ['src/main/res', "${DEBUG_DIR}res"]
                    }
                }

            }else{
                project.apply plugin: 'com.android.library'
            }

        }

        addComponentDependencyMethod(project)
    }

    //需要集成打包相关的task
    static final String TASK_TYPES = ".*((((ASSEMBLE)|(BUILD)|(INSTALL)|((BUILD)?TINKER)|(RESGUARD)).*)|(ASR)|(ASD))"
    static void initByTask(Project project) {
        //参考
        //  ./gradlew :demo:xxxBuildPatch -PccMain=demo //用某插件化框架脚本为demo打补丁包
        //  ./gradlew :demo_component_b:assembleRelease -PccMain=anyothermodules //为demo_b打aar包

        def taskNames = project.gradle.startParameter.taskNames
        def allModuleBuildApkPattern = Pattern.compile(TASK_TYPES)
        print "initByTask project.name:${project.name}"
        println()
        for (String task : taskNames) {
            print "initByTask taskName: ${task},"
            if (allModuleBuildApkPattern.matcher(task.toUpperCase()).matches()) {
                taskIsAssemble = true
                if (task.contains(":")) {
                    def arr = task.split(":")
                    mainModuleName = arr[arr.length - 2].trim()
                }
                break
            }
        }
        println()
    }

    static boolean isMainApp(Project project) {
        return project.ext.has(MODULE_MAIN_APP) && project.ext.mainApp
    }

    static void addComponentDependencyMethod(Project project) {
        //taskIsAssemble 是需要依赖打包的
        //

//        def curModuleIsBuildingApk = taskIsAssemble && (mainModuleName == null && isMainApp(project) || mainModuleName == project.name)

        //当前task 是module以application单独debug运行task 则无需把自己module添加依赖到其他地方，因为他不是library工程了
        //如何判断当前任务是 以application单独debug运行task的library
        boolean isModuleDebugRun = !isMainApp(project) && taskIsAssemble && (mainModuleName == project.name)
        println "addComponentDependencyMethod isModuleDebugRun:${isModuleDebugRun}, isMainApp：${isMainApp(project)}, taskIsAssemble:$taskIsAssemble," +
                " mainModuleName:$mainModuleName, project.name:$project.name"+
                " ${mainModuleName == project.name}"
        // 组件名字taskName
//        project.ext.addModule = { dependencyName, realDependency = null ->
//            if(isModuleDebugRun){
//                return
//            }
//            println "${dependencyName} "
//            def componentProject = project.rootProject.subprojects.find { it.name == dependencyName }
//            def dependencyMode = GradleVersion.version(project.gradle.gradleVersion) >= GradleVersion.version('4.1') ? 'api' : 'compile'
//            project.dependencies.add("api", project.project(":module_personalcenter"))
//            if (realDependency) {// todo 这个没理解？
//                //通过参数传递的依赖方式，如：
//                // project(':moduleName')
//                // 或
//                // 'com.billy.demo:demoA:1.1.0'
//                project.dependencies.add(dependencyMode, realDependency)
//                println "ModularizationPlugin 1>>>> $dependencyMode $realDependency to ${project.name}'s dependencies"
//            } else if (componentProject) {
//                //第二个参数未传，默认为按照module来进行依赖
//                project.dependencies.add(dependencyMode, project.project(":$dependencyName"))
//                println "ModularizationPlugin 2>>>> $dependencyMode project(\":$dependencyName\") to ${project.name}'s dependencies"
//            } else {
//                throw new RuntimeException(
//                        "ModularizationPlugin >>>> add dependency by [ addComponent '$dependencyName' ] occurred an error:" +
//                                "\n'$dependencyName' is not a module in current project" +
//                                " and the 2nd param is not specified for realDependency" +
//                                "\nPlease make sure the module name is '$dependencyName'" +
//                                "\nelse" +
//                                "\nyou can specify the real dependency via add the 2nd param, for example: " +
//                                "addComponent '$dependencyName', 'com.billy.demo:demoB:1.1.0'")
//            }
//        }
        //是否 需要把依赖添加 task

    }

}