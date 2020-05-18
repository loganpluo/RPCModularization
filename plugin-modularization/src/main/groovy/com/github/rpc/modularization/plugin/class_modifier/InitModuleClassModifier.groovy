package com.github.rpc.modularization.plugin.class_modifier

import com.github.rpc.modularization.plugin.incremental_compile.ScanResult
import com.github.rpc.modularization.plugin.scan.ClassInfo
import com.github.rpc.modularization.plugin.util.LogUtil
import com.github.rpc.modularization.plugin.code_generator.ICodeGenerator
import com.github.rpc.modularization.plugin.code_generator.InitModuleCodeGenerator


class InitModuleClassModifier extends ClassModifier {

     private static final TAG = "InitModuleClassModifier"

     @Override
     boolean recordClassModifierTarget(ClassInfo classInfo) {

         String destFilePath = classInfo.destFilePath
         String name = classInfo.name
         String[] interfaces = classInfo.interfaces
         LogUtil.d(TAG,"recordClassModifierTarget name:$name, codeInsertToClass,${classModifierConfig.codeInsertToClass} scanInterface:${classModifierConfig.scanInterface}")

         def result = false
         if(classModifierConfig.codeInsertToClass == name){
             codeInsertToClassFile = new File(destFilePath)
             LogUtil.i(TAG,"recordClassModifierTarget success codeInsertToClass:${classModifierConfig.codeInsertToClass}")
             LogUtil.i(TAG,"recordClassModifierTarget success codeInsertToClassFile success name:$name destFile:$destFilePath")
             result = true
             def scanResult = new ScanResult()
             scanResult.codeInsertToClassFilePath = destFilePath
             getScanResultCacheService().updateScanResult(destFilePath, scanResult)

         }

         interfaces.each {
             if(it == classModifierConfig.scanInterface){
                 LogUtil.i(TAG,"recordClassModifierTarget success scanInterface scanInterface:${classModifierConfig.scanInterface}")
                 LogUtil.i(TAG,"recordClassModifierTarget success classList  name:$name destFile:$destFilePath")
                 classList.add(name)
                 def scanResult = new ScanResult()
                 scanResult.classList.add(name)
                 getScanResultCacheService().updateScanResult(destFilePath, scanResult)
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