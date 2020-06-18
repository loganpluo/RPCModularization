package com.github.rpc.modularization.plugin.incremental_compile

import com.github.rpc.modularization.plugin.class_modifier.ClassModifier
import org.gradle.api.Project


interface IScanResultCacheService {

    /**
     * 增量编译，加载上次扫描结果的缓存
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
     * 增量编译后，最新扫描结果会同步到 缓存；把扫描结果(也包括未变动class jar 扫描结果)应用到 ClassModifier配置
     * @param classModifier 字节码配置
     */
    void applyScanResultCache(ClassModifier classModifier)

    /**
     * 把文件或jar包扫描结果 同步到内存缓存里面
     * @param destFile
     * @param scanResult
     */
    void updateScanResult(String destFile, ScanResult scanResult)


    /**
     * 全部扫描完毕之后，把最新内存扫描结果同步到本地文件缓存
     */
    void saveScanResultCache()

}