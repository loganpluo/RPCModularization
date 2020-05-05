package com.github.rpc.modularization.plugin.class_modifier

import com.github.rpc.modularization.plugin.scan.ClassInfo
import com.github.rpc.modularization.plugin.util.LogUtil
import com.github.rpc.modularization.plugin.code_generator.ICodeGenerator
import com.github.rpc.modularization.plugin.code_generator.InitModuleCodeGenerator


class InitModuleClassModifier extends ClassModifier {

     private static final TAG = "InitModuleClassModifier"

     @Override
     boolean recordClassModifierTarget(ClassInfo classInfo) {

         String destFile = classInfo.destFilePath
         String name = classInfo.name
         String[] interfaces = classInfo.interfaces
         LogUtil.d(TAG,"recordClassModifierTarget name:$name, codeInsertToClass,${classModifierConfig.codeInsertToClass} scanInterface:${classModifierConfig.scanInterface}")

         def result = false
         if(classModifierConfig.codeInsertToClass == name){
             codeInsertToClassFile = new File(destFile)
             LogUtil.i(TAG,"recordClassModifierTarget success codeInsertToClass:${classModifierConfig.codeInsertToClass}")
             LogUtil.i(TAG,"recordClassModifierTarget success codeInsertToClassFile success name:$name destFile:$destFile")
             result = true
         }

         interfaces.each {
             if(it == classModifierConfig.scanInterface){
                 LogUtil.i(TAG,"recordClassModifierTarget success scanInterface scanInterface:${classModifierConfig.scanInterface}")
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