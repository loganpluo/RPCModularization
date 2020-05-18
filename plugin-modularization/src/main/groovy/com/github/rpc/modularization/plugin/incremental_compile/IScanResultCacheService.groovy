package com.github.rpc.modularization.plugin.incremental_compile

import com.github.rpc.modularization.plugin.class_modifier.ClassModifier
import org.gradle.api.Project


interface IScanResultCacheService {

    /**
     * 缓存形式
     * Map<String,ScanResult>
     */
    void loadScanResultCache(Project project)

    /**
     * case 1: 如果是变动目录文件，则直接移除scanFileOrJar 对应的缓存扫描结果， 但是会输出新的目录，之后得更新一下
     * case2： 如果是变动jar包， 则直接移除scanFileOrJar 对应的缓存扫描结果
     * @param scanFileOrJar: 扫描原始的class文件(应该都是一样的)
     */
    void removeScanResultCache(String scanFileOrJarPath)

    /**
     * case 1: 如果是目录文件，则直接查找，命中则直接add到ClassModifier，返回true
     * case 2: 如果是jar文件，则直接查找，命中则直接add到ClassModifier，返回true
     * @param scanFileOrJar
     * @param ClassModifier
     * @return
     */
    boolean setScanResultFromCache(String scanFileOrJarPath, ClassModifier ClassModifier)


    void applyScanResultCache(ClassModifier classModifier)

    void updateScanResult(String destFile, ScanResult scanResult)


    /**
     * 全部扫描完毕之后 再进行缓存的更新
     */
    void saveScanResultCache()

}