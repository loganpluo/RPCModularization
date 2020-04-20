package com.github.rpc.modularization.plugin

import org.gradle.api.Project
import org.gradle.util.GradleVersion

import java.util.regex.Pattern

class ModuleDebugAbleHelper {

    static final String DEBUG_DIR = "src/main/debug/"
    static final String MODULE_MAIN_APP = "mainApp"

    static String mainTaskName// 执行的任务名词
    static String mainAppName// 主工程的名字

    static boolean doDebugAbleModule(Project project) {

        initByTask(project)

        if(isMainApp(project)){//主app工程一直以application运行
            project.apply plugin: 'com.android.application'
        }else{
            boolean isDebugAlone = Boolean.parseBoolean((project.properties.get("isDebugAlone")))
            configDebugModelGradle(project,isDebugAlone)
            addModuleForDebugModel(project,isDebugAlone)
        }

        addModuleDependencyMethod(project)
    }

    static boolean isMainApp(Project project) {
        return  mainAppName == project.name
    }

    //需要集成打包相关的task
    static final String TASK_TYPES = ".*((((ASSEMBLE)|(BUILD)|(INSTALL)|((BUILD)?TINKER)|(RESGUARD)).*)|(ASR)|(ASD))"
    static void initByTask(Project project) {
        if(mainAppName == null){
            mainAppName = project.properties.get('mainAppName')
            if(mainAppName == null){
                throw new RuntimeException("must set mainAppName property in root gradle.properties file")
            }
        }

        mainTaskName = null
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
                if (task.contains(":")) {
                    def arr = task.split(":")
                    mainTaskName = arr[arr.length - 2].trim()
                }
                break
            }
        }
        print "initByTask mainAppName：$mainAppName, mainTaskName:$mainTaskName"
        println()
    }

    /**
     * 是否为app主工程编译任务
     * @param mainModuleName
     * @return
     */
    static boolean isMainAppBuild(String mainTaskName){
        return mainTaskName == mainAppName
    }

    /**
     * 配置可以单独调试运行的library模块
     * config阶段 library模块 必须是 com.android.application工程，才会出现在 as的 可以run的选项
     * @param project
     */
    static void configDebugModelGradle(Project project, Boolean isDebugAlone){

        boolean isMainAppBuild = isMainAppBuild(mainTaskName)
        println "mainModuleName:$mainTaskName, isMainAppBuild:$isMainAppBuild"
        if (!isDebugAlone || isMainAppBuild) {//非可单独编译 或者 如果是app主工程编译任务 则 都为library
            //todo 还需要剔除调模块打包的情况
            project.apply plugin: 'com.android.library'
            return
        }

        //模块可以单独run
        project.apply plugin: 'com.android.application'
        println "project.android.applicationId ${project.properties.get("moduleApplicationId")}"
        project.android.defaultConfig.applicationId = project.properties.get("moduleApplicationId")//'com.github.rpc.module_personalcenter.run'

        println "project.android.sourceSets.main"

        project.android.sourceSets.main {
            //run模式下，如果存在src/main/debug/AndroidManifest.xml，则自动使用其作为manifest文件
            def debugManifest = getDebugMergeManifest(project)//"${DEBUG_DIR}AndroidManifest.xml"
            println "debugManifest getDebugMergeManifest $debugManifest"
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

    }

    static String getDebugMergeManifest(Project project){
//        def debugManifest = "${DEBUG_DIR}AndroidManifest.xml"
//        println "debugManifest"
//        if (project.file(debugManifest).exists()) {
//            manifest.srcFile debugMergeManifest
//        }
        //todo merge 操作
        return "src/main/debug/build/AndroidManifest.xml"
    }

    /**
     * gradle配置支持 addModule关键字，实现 动态依赖 业务模块支持， 注意只用在主工程依赖 业务实现模块
     *
     * @param project
     */
    static void addModuleDependencyMethod(Project project) {

        boolean isMainAppBuild = isMainAppBuild(mainTaskName)
        println "addModuleDependencyMethod isMainAppBuild:$isMainAppBuild"
        project.ext.addModule = { dependencyName, realDependency = null ->
            if(!isMainAppBuild){//只要是主工程编译任务则 添加 实现模块
                return
            }
            println "project.dependencies.add ${dependencyName} "
            def componentProject = project.rootProject.subprojects.find { it.name == dependencyName }
            def dependencyMode = GradleVersion.version(project.gradle.gradleVersion) >= GradleVersion.version('4.1') ? 'api' : 'compile'
            if (realDependency) {// todo 这个没理解？
                //通过参数传递的依赖方式，如：
                // project(':moduleName')
                // 或
                // 'com.billy.demo:demoA:1.1.0'
                project.dependencies.add(dependencyMode, realDependency)
                println "ModularizationPlugin 1>>>> $dependencyMode $realDependency to ${project.name}'s dependencies"
            } else if (componentProject) {
                //第二个参数未传，默认为按照module来进行依赖
                project.dependencies.add(dependencyMode, project.project(":$dependencyName"))
                println "ModularizationPlugin 2>>>> $dependencyMode project(\":$dependencyName\") to ${project.name}'s dependencies"
            } else {
                throw new RuntimeException(
                        "ModularizationPlugin >>>> add dependency by [ addModule '$dependencyName' ] occurred an error:" +
                                "\n'$dependencyName' is not a module in current project" +
                                " and the 2nd param is not specified for realDependency" +
                                "\nPlease make sure the module name is '$dependencyName'" +
                                "\nelse" +
                                "\nyou can specify the real dependency via add the 2nd param, for example: " +
                                "addModule '$dependencyName', 'com.billy.demo:demoB:1.1.0'")
            }
        }

    }

    /**
     *  独立debug 运行模块 通过gradle.properties的addModules配置 依赖的业务实现模块
     * @param project
     */
    static void addModuleForDebugModel(Project project,Boolean isDebugAlone){
        boolean isModuleDebugRun = isDebugAlone && (mainTaskName == project.name)
        println "addModuleForDebugModel " +
                "isModuleDebugRun:$isModuleDebugRun, " +
                "isDebugAlone:$isDebugAlone, mainTaskName:$mainTaskName, project.name:${project.name}"
        if(!isModuleDebugRun) return

        String modules = (String)project.properties.get("addModules")

        if (modules == null || modules.length() == 0) {
            System.out.println("there is no add dependencies ")
            return
        }
        String[] compileModules = modules.split(",")
        if (compileModules == null || compileModules.length == 0) {
            System.out.println("there is no add dependencies ")
            return
        }
        for (String module : compileModules) {
            System.out.println("comp is " + module)
            if (module.contains(":")) {
                /**
                 * 示例语法:groupId:artifactId:version(@aar)
                 * compileComponent=com.luojilab.reader:readercomponent:1.0.0
                 * 注意，前提是已经将组件aar文件发布到maven上，并配置了相应的repositories
                 */
                project.dependencies.add("compile", module)
                System.out.println("add dependencies lib  : " + module)
            } else {
                /**
                 * 示例语法:module
                 * compileComponent=readercomponent,sharecomponent
                 */
                project.dependencies.add("compile", project.project(':' + module))
                System.out.println("add dependencies project : " + module)
            }
        }

    }

}