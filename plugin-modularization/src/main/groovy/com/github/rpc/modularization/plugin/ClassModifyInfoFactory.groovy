package com.github.rpc.modularization.plugin

class ClassModifyInfoFactory{


    static ClassModifier getClassModifier(ClassModifierConfig config){
        ClassModifierType.values().each {
            if(config.type == it.type){
                ClassModifier classModifier = it.classModifyClass.newInstance()
                classModifier.setClassModifierConfig(config)
            }

        }

    }



}