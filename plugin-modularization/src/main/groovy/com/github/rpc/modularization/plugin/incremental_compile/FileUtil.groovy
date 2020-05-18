package com.github.rpc.modularization.plugin.incremental_compile

import com.android.builder.model.AndroidProject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.gradle.api.Project

import java.lang.reflect.Type

class FileUtil{

    static File getCacheFile(Project project, String fileName) {
        String baseDir = getCacheFileDir(project)
        if (mkdirs(baseDir)) {
            return new File(baseDir + fileName)
        } else {
            throw new FileNotFoundException("Not found  path:" + baseDir)
        }
    }

    static boolean mkdirs(String dirPath) {
        def baseDirFile = new File(dirPath)
        def isSuccess = true
        if (!baseDirFile.isDirectory()) {
            isSuccess = baseDirFile.mkdirs()
        }
        return isSuccess
    }


    final static def CACHE_INFO_DIR = "plugin-modularization"
    private static String getCacheFileDir(Project project) {
        return project.getBuildDir().absolutePath +
                File.separator + AndroidProject.FD_INTERMEDIATES +
                File.separator + CACHE_INFO_DIR + File.separator
    }

    static Map<String,ScanResult> parse(File file,Type type){

        if (!file.exists()) {
            return null
        }

        def text = file.text
        if (text) {
            try {
                return new Gson().fromJson(text, type)
            } catch (Exception e) {
                e.printStackTrace()
            }
        }

    }

}