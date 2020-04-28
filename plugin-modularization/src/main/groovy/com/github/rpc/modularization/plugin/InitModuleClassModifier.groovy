package com.github.rpc.modularization.plugin


class InitModuleClassModifier extends ClassModifier {

     private static final TAG = "InitModuleClassModifier"

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
     ICodeGenerator getICodeGenerator() {
         return new InitModuleCodeGenerator()
     }
 }