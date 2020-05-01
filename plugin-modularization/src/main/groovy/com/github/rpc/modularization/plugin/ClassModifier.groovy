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
        classModifierConfig.type = ClassModifierType.InterfaceModuleInit.type
        classModifierConfig.scanInterface = config.get("scanInterface") ?
                convertDotToSlash(config.get("scanInterface")) : ""

        classModifierConfig.scanAnnotation = config.get("scanAnnotation") ?
                convertDotToSlash(config.get("scanAnnotation")) : ""

        classModifierConfig.codeInsertToClass = config.get("codeInsertToClass") ?
                convertDotToSlash(config.get("codeInsertToClass")) : ""

        classModifierConfig.codeInsertToMethod = config.get("codeInsertToMethod") ?
                config.get("codeInsertToMethod") : ""

        //todo 多个参数支持
        String codeInsertToMethodParams = config.get("codeInsertToMethodParams") ?
                convertDotToSlash(config.get("codeInsertToMethodParams")) : ""
        classModifierConfig.codeInsertToMethodParams = codeInsertToMethodParams

        classModifierConfig.callMethodName = config.get("callMethodName") ?
                config.get("callMethodName") : ""
        //todo 多个参数支持
        classModifierConfig.callMethodParams = config.get("callMethodParams") ?
                convertDotToSlash(config.get("callMethodParams")) : ""

        //todo 提出
        ArrayList<String> exclude;

        LogUtil.i("ClassModifier"," ${getClass()} parse classModifierConfig:$classModifierConfig")
    }
    abstract ICodeGenerator getICodeGenerator()

    static String convertDotToSlash(String str) {
        return str ? str.replaceAll('\\.', '/').intern() : str
    }


}