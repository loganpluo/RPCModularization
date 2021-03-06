package com.github.rpc.modularization.plugin.incremental_compile


import com.github.rpc.modularization.plugin.class_modifier.ClassModifier
import com.github.rpc.modularization.plugin.util.LogUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.gradle.api.Project

abstract class CommScanCacheService implements IScanResultCacheService {


    protected Map<String,ScanResult> scanResultCacheMap = new HashMap<>()
    protected File cacheFile

    @Override
    void loadScanResultCache(Project project) {
        cacheFile = FileUtil.getCacheFile(project,getCacheFileName())
        Map<String,ScanResult> resultMap = FileUtil.parse(cacheFile, new TypeToken<HashMap<String, ScanResult>>() {
        }.getType())
        LogUtil.i(getTag(),"loadScanResultCache cacheFile:$cacheFile")
        LogUtil.i(getTag(),"loadScanResultCache resultMap:$resultMap")
        if(resultMap != null){
            scanResultCacheMap.putAll(resultMap)
        }
    }

    abstract String getCacheFileName();

    abstract String getTag();


    @Override
    void removeScanResultCache(String scanFileOrJarPath) {
        scanResultCacheMap.remove(scanFileOrJarPath)
        LogUtil.i(getTag(),"removeScanResultCache isSuccess:${scanResultCacheMap.containsKey(scanFileOrJarPath)} " +
                "scanFileOrJarPath:$scanFileOrJarPath")
    }

    @Override
    void applyScanResultCache(ClassModifier classModifier) {
        scanResultCacheMap.each {
            LogUtil.i(getTag(),"applyScanResultCache key:"+it.key)
            if(it.value.codeInsertToClassFilePath != null &&
                    it.value.codeInsertToClassFilePath.length() > 0){
                classModifier.codeInsertToClassFile = new File(it.value.codeInsertToClassFilePath)
                LogUtil.i(getTag(),"applyScanResultCache codeInsertToClassFile:${classModifier.codeInsertToClassFile}")
            }

            if(it.value.classList != null && it.value.classList.size() > 0){
                LogUtil.i(getTag(),"applyScanResultCache classList:${it.value.classList}")
                classModifier.classList.addAll(it.value.classList)
            }

        }
    }

    @Override
    void updateScanResult(String destFilePath, ScanResult scanResult) {
        LogUtil.i(getTag(),"updateScanResult " +
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
                LogUtil.i(getTag(),"saveScanResultCache scanResultCacheMapString:$scanResultCacheMapString")
                cacheFile.write(scanResultCacheMapString)
            }
        }catch(Throwable throwable){
            throwable.printStackTrace()
        }

    }



}