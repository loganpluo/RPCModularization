package com.github.rpc.modularization.plugin

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
        if("com/github/rpc/modularization/RPCModuleServiceManager" == name){
            codeInsertToClassFile = new File(destFile)
            LogUtil.i(TAG,"recordClassModifierTarget success codeInsertToClassFile success name:$name destFile:$destFile")
            return true
        }


        //识别出注解@ModuleService 注解的实现serviceimpl
        if("Lcom/github/rpc/modularization/ModuleService;" == classInfo.annotationDesc){
            classList.add(name)
        }

        return false
    }

    @Override
    ICodeGenerator getICodeGenerator() {
        return new RegisterModuleServiceCodeGenerator()
    }
}