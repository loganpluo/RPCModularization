package com.github.rpc.modularization.plugin.incremental_compile


import com.github.rpc.modularization.plugin.class_modifier.ClassModifier
import com.github.rpc.modularization.plugin.util.LogUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.gradle.api.Project

class RegisterModuleScanCacheService implements IScanResultCacheService {

    private final static String TAG = "RegisterModuleScanCacheService"

    private Map<String,ScanResult> scanResultCacheMap = new HashMap<>()
    private File cacheFile

    @Override
    void loadScanResultCache(Project project) {
        cacheFile = FileUtil.getCacheFile(project,"RegisterModule.json")
        Map<String,ScanResult> resultMap = FileUtil.parse(cacheFile, new TypeToken<HashMap<String, ScanResult>>() {
        }.getType())
        LogUtil.d(TAG,"loadScanResultCache cacheFile:$cacheFile")
        LogUtil.d(TAG,"loadScanResultCache resultMap:$resultMap")
        if(resultMap != null){
            scanResultCacheMap.putAll(resultMap)
        }
    }



    @Override
    void removeScanResultCache(String scanFileOrJarPath) {
        scanResultCacheMap.remove(scanFileOrJarPath)
    }

    @Override
    void applyScanResultCache(ClassModifier classModifier) {
        scanResultCacheMap.each {
            LogUtil.d(TAG,"applyScanResultCache key:"+it.key)
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