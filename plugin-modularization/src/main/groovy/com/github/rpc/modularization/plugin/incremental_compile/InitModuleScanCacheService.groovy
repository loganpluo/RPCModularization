package com.github.rpc.modularization.plugin.incremental_compile

import com.android.builder.model.AndroidProject
import com.github.rpc.modularization.plugin.class_modifier.ClassModifier
import com.github.rpc.modularization.plugin.util.LogUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.gradle.api.Project


class InitModuleScanCacheService implements IScanResultCacheService {

    private final static String TAG = "InitModuleScanCacheService"

    private Map<String,ScanResult> scanResultCacheMap = new HashMap<>()
    private File cacheFile

    @Override
    void loadScanResultCache(Project project) {
        cacheFile = FileUtil.getCacheFile(project,"InitModule.json")
        LogUtil.d(TAG,"loadScanResultCache cacheFile:$cacheFile")
        Map<String,ScanResult> resultMap = FileUtil.parse(cacheFile, new TypeToken<HashMap<String, ScanResult>>() {
        }.getType())
        if(resultMap != null){
            scanResultCacheMap.putAll(resultMap)
        }
    }

    @Override
    void removeScanResultCache(String scanFileOrJarPath) {
        scanResultCacheMap.remove(scanFileOrJarPath)
    }

    @Override
    boolean setScanResultFromCache(String scanFileOrJarPath, ClassModifier classModifier) {
        def scanResult =
                scanResultCacheMap.get(scanFileOrJarPath)

        if(scanResult != null){
            LogUtil.d(TAG,"setScanResultFromCache " +
                    "scanFileOrJarPath:$scanFileOrJarPath " +
                    "codeInsertToClassFile:${scanResult.codeInsertToClassFilePath} " +
                    "classList:${ scanResult.classList }")
            if(scanResult.codeInsertToClassFilePath != null &&
               scanResult.codeInsertToClassFilePath.length() > 0){
                classModifier.codeInsertToClassFile = new File(scanResult.codeInsertToClassFilePath)
            }

            if(scanResult.classList != null && scanResult.classList.size() > 0 ){
                classModifier.classList.addAll(scanResult.classList)
            }
            return true
        }

        return false
    }

    void applyScanResultCache(ClassModifier classModifier){
        scanResultCacheMap.each {
            if(it.value.codeInsertToClassFilePath != null &&
               it.value.codeInsertToClassFilePath.length() > 0){
                classModifier.codeInsertToClassFile = new File(it.value.codeInsertToClassFilePath)
                LogUtil.d(TAG,"applyScanResultCache codeInsertToClassFile:${classModifier.codeInsertToClassFile}")
            }

            if(it.value.classList != null && it.value.classList.size() > 0){
                LogUtil.d(TAG,"applyScanResultCache classList:${it.value.classList}")
                classModifier.classList.addAll(it.value.classList)
            }

        }
    }

    @Override
    void updateScanResult(String destFilePath, ScanResult scanResult) {
        LogUtil.d(TAG,"updateScanResult " +
                "destFilePath:$destFilePath " +
                "codeInsertToClassFilePath:${scanResult.codeInsertToClassFilePath} " +
                "classList:${ scanResult.classList }")

        ScanResult cacheScanResult = scanResultCacheMap.get(destFilePath)
        if(cacheScanResult == null){
            scanResultCacheMap.put(destFilePath, scanResult)
            return
        }

        if(scanResult.codeInsertToClassFilePath != null
           && scanResult.codeInsertToClassFilePath.length() > 0){
            cacheScanResult.codeInsertToClassFilePath = scanResult.codeInsertToClassFilePath
        }

        cacheScanResult.classList.addAll(scanResult.classList)

    }


    @Override
    void saveScanResultCache() {

        try{
            if(cacheFile != null){
                def scanResultCacheMapString = new Gson().toJson(scanResultCacheMap)
                LogUtil.d(TAG,"saveScanResultCache scanResultCacheMapString:$scanResultCacheMapString")
                cacheFile.write(scanResultCacheMapString)
            }
        }catch(Throwable throwable){
            throwable.printStackTrace()
        }

    }



}