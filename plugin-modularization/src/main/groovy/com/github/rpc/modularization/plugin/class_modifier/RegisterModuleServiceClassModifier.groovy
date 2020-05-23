package com.github.rpc.modularization.plugin.class_modifier

import com.github.rpc.modularization.plugin.config.ClassModifierConfig
import com.github.rpc.modularization.plugin.incremental_compile.ScanResult
import com.github.rpc.modularization.plugin.scan.ClassInfo
import com.github.rpc.modularization.plugin.util.LogUtil
import com.github.rpc.modularization.plugin.code_generator.ICodeGenerator
import com.github.rpc.modularization.plugin.code_generator.RegisterModuleServiceCodeGenerator

class RegisterModuleServiceClassModifier extends ClassModifier {

    private static final String TAG = "RegisterModuleServiceClassModifier"

    @Override
    protected ClassModifierConfig createClassModifierConfig() {
        return super.createClassModifierConfig()
    }

    @Override
    boolean recordClassModifierTarget(ClassInfo classInfo) {
        String destFilePath = classInfo.destFilePath
        String name = classInfo.name
        LogUtil.d(TAG,"recordClassModifierTarget")
        //识别 被修改的class
        if(classModifierConfig.codeInsertToClass == name){
            LogUtil.i(TAG,"recordClassModifierTarget success codeInsertToClass:${classModifierConfig.codeInsertToClass}")
            codeInsertToClassFile = new File(destFilePath)
            LogUtil.i(TAG,"recordClassModifierTarget success codeInsertToClassFile success name:$name destFile:$destFilePath")
            def scanResult = new ScanResult()
            scanResult.codeInsertToClassFilePath = destFilePath
            getScanResultCacheService().updateScanResult(destFilePath, scanResult)
            return true
        }


        //识别出注解@ModuleService 注解的实现serviceimpl
        if("L${classModifierConfig.scanAnnotation};" == classInfo.annotationDesc){
            LogUtil.i(TAG,"recordClassModifierTarget  success scanAnnotation:${classModifierConfig.scanAnnotation}")
            LogUtil.i(TAG,"recordClassModifierTarget  success Annotation name:${name} destFile:$destFilePath")
            classList.add(name)
            def scanResult = new ScanResult()
            scanResult.classList.add(name)
            getScanResultCacheService().updateScanResult(destFilePath, scanResult)
        }

        return false
    }

    @Override
    ICodeGenerator getICodeGenerator() {
        return new RegisterModuleServiceCodeGenerator()
    }
}