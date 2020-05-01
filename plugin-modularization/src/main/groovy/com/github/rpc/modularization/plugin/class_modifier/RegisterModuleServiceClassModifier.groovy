package com.github.rpc.modularization.plugin.class_modifier

import com.github.rpc.modularization.plugin.scan.ClassInfo
import com.github.rpc.modularization.plugin.util.LogUtil
import com.github.rpc.modularization.plugin.code_generator.ICodeGenerator
import com.github.rpc.modularization.plugin.code_generator.RegisterModuleServiceCodeGenerator

class RegisterModuleServiceClassModifier extends ClassModifier {

    private static final String TAG = "RegisterModuleServiceClassModifier"

    @Override
    boolean isNeedScanAnnotation() {
        return true
    }

    @Override
    boolean recordClassModifierTarget(ClassInfo classInfo) {
        String destFile = classInfo.destFilePath
        String name = classInfo.name
        LogUtil.d(TAG,"recordClassModifierTarget")
        //识别 被修改的class
        if(classModifierConfig.codeInsertToClass == name){
            LogUtil.i(TAG,"recordClassModifierTarget success codeInsertToClass:${classModifierConfig.codeInsertToClass}")
            codeInsertToClassFile = new File(destFile)
            LogUtil.i(TAG,"recordClassModifierTarget success codeInsertToClassFile success name:$name destFile:$destFile")
            return true
        }


        //识别出注解@ModuleService 注解的实现serviceimpl
        if("L${classModifierConfig.scanAnnotation};" == classInfo.annotationDesc){
            LogUtil.i(TAG,"recordClassModifierTarget  success scanAnnotation:${classModifierConfig.scanAnnotation}")
            LogUtil.i(TAG,"recordClassModifierTarget  success Annotation name:${name}")
            classList.add(name)
        }

        return false
    }

    @Override
    ICodeGenerator getICodeGenerator() {
        return new RegisterModuleServiceCodeGenerator()
    }
}