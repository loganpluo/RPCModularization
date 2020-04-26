package com.github.rpc.modularization.plugin

import org.gradle.api.Project

class ScanHelper {

    static String TAG = "AutoRegister"

    /**
     *  扫描目录 找到
     *  //收集到待注入的模块 和 模块服务实现
     *  //检测到被注入模块的class
     * @param path
     * @param project
     */
    static void scanDirectory(String path, Project project){
        LogUtil.d(TAG,"test")
        File dir = new File(path)
        if (dir.isDirectory()) {
            dir.eachFileRecurse { File file ->
                String filePath = file.absolutePath
                LogUtil.d(TAG,"filePath: $filePath ， file.isFile:${file.isFile()}")
                if(file.isFile()){//
                    //收集到待注入的模块 和 模块服务实现
                    //检测到被注入模块的class
                }
            }
            //directoryInput.changedFiles
        }
    }


}