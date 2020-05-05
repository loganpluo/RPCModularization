package com.github.rpc.modularization.plugin

import com.github.rpc.modularization.plugin.util.LogUtil
import org.gradle.api.Project

class ApiDirHelper{

    static final String TAG = "ApiDirHelper"

    static void apiDir(Project project){
        // 只需要在setting的时候能识别就可以了
        //需要判断是不是app run 或者 library单独编译打包，是的话 则 需要去掉
        // 什么情况需要加入进来， 只有在编写代码的时候

        //目标为了防止在这个目录下建立 java 文件，app run 或者 publish 把 java文件打包进来

        def taskNames = project.gradle.startParameter.taskNames
        LogUtil.i(TAG,"apiDir taskNames:$taskNames")
        LogUtil.i(TAG,"apiDir module_iml_api_src:${project.getProperties().get("module_iml_api_src")}")
        def apiDir = project.getProperties().get("module_iml_api_src")

        project.android.sourceSets.main {
            if (project.file("$apiDir").exists()) {
                LogUtil.i(TAG,"set $apiDir success")
                java.srcDirs = ['src/main/java', "$apiDir"]
            }
        }

    }

}