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

        LogUtil.d(TAG,"setScanResultFromCache " +
                "scanFileOrJarPath:$scanFileOrJarPath " +
                "codeInsertToClassFile:${classModifier.codeInsertToClassFile} " +
                "classList:${ classModifier.classList }")

        if(scanResult != null){
            classModifier.codeInsertToClassFile = new File(scanResult.codeInsertToClassFilePath)
            classModifier.classList.addAll(scanResult.classList)
            return true
        }

        return false
    }

    @Override
    void applyScanResultCache(ClassModifier classModifier) {

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
                cacheFile.write(new Gson().toJson(scanResultCacheMap))
            }
        }catch(Throwable throwable){
            throwable.printStackTrace()
        }

    }



}