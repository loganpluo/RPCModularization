package com.github.rpc.modularization.plugin


class ParseConfigUtil{

    static final String TAG = "ParseConfigUtil"

    static void parse(ClassModifierConfig classModifierConfig,
                      Map<String, Object> config){

        classModifierConfig.type = ClassModifierType.InterfaceModuleInit.type
        classModifierConfig.scanInterface = config.get("scanInterface") ?
                convertDotToSlash(config.get("scanInterface")) : ""

        classModifierConfig.scanAnnotation = config.get("scanAnnotation") ?
                convertDotToSlash(config.get("scanAnnotation")) : ""

        classModifierConfig.codeInsertToClass = config.get("codeInsertToClass") ?
                convertDotToSlash(config.get("codeInsertToClass")) : ""

        classModifierConfig.codeInsertToMethod = config.get("codeInsertToMethod") ?
                config.get("codeInsertToMethod") : ""

        String codeInsertToMethodParams = config.get("codeInsertToMethodParams") ?
                convertDotToSlash(config.get("codeInsertToMethodParams")) : ""
        classModifierConfig.codeInsertToMethodParams = ClassInfoUtil.getDescMethodParamsConfig(codeInsertToMethodParams)

        LogUtil.d(TAG,"codeInsertToMethodLocalVariables: ${config.get("codeInsertToMethodLocalVariables")}")

        ArrayList<Map<String, Object>> localVariablesConfig = config.get("codeInsertToMethodLocalVariables")
        if(localVariablesConfig != null){
            classModifierConfig.localVariables = new ArrayList<>()
            localVariablesConfig.each {
                String localVariableName =  it.get('name')
                String localVariableType =  it.get('type')
                if(localVariableName){
                    LocalVariable localVariable = new LocalVariable(localVariableName,
                                                    ClassInfoUtil.getDescLocalVariableConfig(localVariableType))
                    classModifierConfig.localVariables.add(localVariable)
                }
            }
        }

        classModifierConfig.callMethodName = config.get("callMethodName") ?
                config.get("callMethodName") : ""

        String callMethodParams = config.get("callMethodParams") ?
                convertDotToSlash(config.get("callMethodParams")) : ""
        classModifierConfig.callMethodParams = ClassInfoUtil.getDescMethodParamsConfig(callMethodParams)

        //todo 提出
        ArrayList<String> exclude;

    }

    static String convertDotToSlash(String str) {
        return ClassInfoUtil.convertDotToSlash(str)
    }

}