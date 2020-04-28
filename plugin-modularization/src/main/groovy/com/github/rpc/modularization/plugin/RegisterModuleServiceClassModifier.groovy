package com.github.rpc.modularization.plugin

class RegisterModuleServiceClassModifier extends ClassModifier {

    private static final String TAG = "RegisterModuleServiceClassModifier"

    @Override
    boolean recordClassModifierTarget(String destFile,
                                   int version, int access, String name,
                                   String signature, String superName, String[] interfaces) {
        //识别 被修改的class
        if("com/github/rpc/modularization/RPCModuleServiceManager" == name){
            codeInsertToClassFile = new File(destFile)
            LogUtil.i(TAG,"recordClassModifierTarget success codeInsertToClassFile success name:$name destFile:$destFile")
            return true
        }


        //识别出注解@ModuleService 注解的实现serviceimpl
        //

        return false
    }

    @Override
    ICodeGenerator getICodeGenerator() {
        return new RegisterModuleServiceCodeGenerator()
    }
}