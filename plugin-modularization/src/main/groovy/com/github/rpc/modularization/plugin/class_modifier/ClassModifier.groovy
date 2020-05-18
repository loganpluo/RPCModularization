package com.github.rpc.modularization.plugin.class_modifier


import com.github.rpc.modularization.plugin.incremental_compile.IScanResultCacheService
import com.github.rpc.modularization.plugin.scan.ClassInfo
import com.github.rpc.modularization.plugin.util.LogUtil
import com.github.rpc.modularization.plugin.code_generator.ICodeGenerator
import com.github.rpc.modularization.plugin.config.ClassModifierConfig
import com.github.rpc.modularization.plugin.config.ParseConfigUtil

abstract class ClassModifier {

    ClassModifierConfig classModifierConfig

    File codeInsertToClassFile;
    Set<String> classList = new HashSet<>();

    public ClassModifier(){
        classModifierConfig = createClassModifierConfig()
    }

    /**
     * 返回自己的解析配置信息, 用于扫描过滤 和 修改字节码信息
     * @return
     */
    protected ClassModifierConfig createClassModifierConfig(){
        return new ClassModifierConfig()
    }

    /**
     * 解析gradle的classmodifier.configs配置 到 classModifierConfig里面
     * @param config
     */
    protected void parse(Map<String, Object> config){
        ParseConfigUtil.parse(classModifierConfig, config)
        LogUtil.i("ClassModifier"," ${getClass()} parse classModifierConfig:$classModifierConfig")
    }

    /**
     * 记录被修改的 codeInsertToClassFile
     * 被注册的类 classList
     * @param classInfo 扫描到的类信息
     */
    abstract boolean recordClassModifierTarget(ClassInfo classInfo)

    /**
     * codeInsertToClassFile 和 classList完整；
     * 被修改类 和 插入的类信息是否收集完整
     * @return
     */
    boolean  hasWholeInfo(){
        return codeInsertToClassFile != null && codeInsertToClassFile.exists() && !classList.isEmpty()
    }

    /**
     * class字节码修改实现
     * @return
     */
    abstract ICodeGenerator getICodeGenerator()

    IScanResultCacheService getScanResultCacheService(){
        return scanResultCacheService
    }

    IScanResultCacheService scanResultCacheService;

    void setScanResultCacheService(IScanResultCacheService scanResultCacheService){
        this.scanResultCacheService = scanResultCacheService
    }


}