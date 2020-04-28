package com.github.rpc.modularization.plugin


class InitModuleClassModifier extends ClassModifier {

     private static final TAG = "InitModuleClassModifier"

     @Override
     boolean isTargetCodeInsertToClassFile(File file) {
         //com.github.rpc.modularization.RPCModuleServiceManager
         //todo 动态获取
         return "RPCModuleServiceManager" == file.getName()
     }

     @Override
     boolean isSetClassFile(File file){
         //classModifierConfig.scanInterface com/github/rpc.modularization/RPCModule
        return "RPCModule" == file.getName()
     }


     @Override
     boolean recordClassModifierTarget(String destFile,
                                    int version, int access, String name,
                                    String signature, String superName, String[] interfaces) {
         LogUtil.d(TAG,"recordClassModifierTarget name:$name, signature:$signature, superName:$superName")
         def result = false
         if("com/github/rpc/modularization/RPCModuleServiceManager" == name){
             codeInsertToClassFile = new File(destFile)
             LogUtil.i(TAG,"recordClassModifierTarget success codeInsertToClassFile success name:$name destFile:$destFile")
             result = true
         }

         interfaces.each {
             if(it == "com/github/rpc/modularization/RPCModule"){
                 LogUtil.i(TAG,"recordClassModifierTarget success classList  name:$name destFile:$destFile")
                 classList.add(name)
                 result = true
             }
         }
         return result
     }

     @Override
     void parse(Map<String, Object> config) {
         classModifierConfig.type = ClassModifierType.InterfaceModuleInit.type
         classModifierConfig.scanInterface = config.get("scanInterface") ?
                                            convertDotToSlash(config.get("scanInterface")) : ""

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
         //android.content.Context,com.github.rpc.modularization.RPCModule
         classModifierConfig.callMethodParams = config.get("callMethodParams") ?
                                   convertDotToSlash(config.get("callMethodParams")) : ""

         //todo 提出
         ArrayList<String> exclude;

         LogUtil.i(TAG,"parse classModifierConfig:$classModifierConfig")

     }

     @Override
     ICodeGenerator getICodeGenerator() {
         return new InitModuleCodeGenerator()
     }
 }