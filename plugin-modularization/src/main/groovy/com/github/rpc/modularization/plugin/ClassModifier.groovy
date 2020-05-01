package com.github.rpc.modularization.plugin

import org.objectweb.asm.MethodVisitor

abstract class ClassModifier {

    ClassModifierConfig classModifierConfig = new ClassModifierConfig()

    File codeInsertToClassFile;
    Set<String> classList = new HashSet<>();

     boolean isNeedScanAnnotation(){
         return false
     }

    /**
     * 记录被修改的 codeInsertToClassFile
     * 被注册的类 classList
     */
    abstract boolean recordClassModifierTarget(ClassInfo classInfo)



    protected void parse(Map<String, Object> config){
        ParseConfigUtil.parse(classModifierConfig, config)
        LogUtil.i("ClassModifier"," ${getClass()} parse classModifierConfig:$classModifierConfig")
    }
    abstract ICodeGenerator getICodeGenerator()

    static String convertDotToSlash(String str) {
        return str ? str.replaceAll('\\.', '/').intern() : str
    }


}