package com.github.rpc.modularization.plugin

class ClassModifyInfoFactory{


    static ClassModifier getClassModifier(String type, Map<String, Object> config){
        ClassModifierType.values().each {
            if(type == it.type){
                ClassModifier classModifier = it.classModifyClass.newInstance()
                classModifier.parse(config)
                return classModifier
            }
        }

        return null
    }



}