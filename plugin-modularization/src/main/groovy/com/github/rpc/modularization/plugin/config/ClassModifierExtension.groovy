package com.github.rpc.modularization.plugin.config

import com.github.rpc.modularization.plugin.class_modifier.ClassModifier
import com.github.rpc.modularization.plugin.class_modifier.ClassModifierType
import org.gradle.api.Project

class ClassModifierExtension {

    public static final String EXT_NAME = 'classmodifier'

    public ArrayList<Map<String, Object>> configs = []

    public Project project

    public List<ClassModifier> classModifiers = new ArrayList<>();

    public void loadConfig(){
        classModifiers.clear()
        configs.each {
            String type = it.get("type")
            ClassModifierType.values().each { classModifierType->
                if(classModifierType.type == type){
                    try{
                        ClassModifier classModifier =
                                classModifierType.classModifyClass.newInstance()
                        classModifier.parse(it)

                        classModifier.
                                setScanResultCacheService(
                                        classModifierType.scanResultCacheServiceClass.newInstance())

                        classModifiers.add(classModifier)
                    }catch (Exception e) {
                        println "发现异常：" + e
                    }
                }
            }
        }
    }


}