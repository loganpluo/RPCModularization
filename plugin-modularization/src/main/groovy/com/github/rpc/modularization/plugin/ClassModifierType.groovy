package com.github.rpc.modularization.plugin

enum ClassModifierType {
    InterfaceModuleInit("InterfaceModuleInit",InitModuleClassModifier.class),//模块自动注册
    AnnotationModuleAutoRegister("AnnotationModuleAutoRegister",RegisterModuleServiceClassModifier.class),//注解模块自动注册

    public String type
    public Class<? extends ClassModifier> classModifyClass;
    public ClassModifierType(String type,Class<? extends ClassModifier> classModifyClass){
        this.type = type
        this.classModifyClass = classModifyClass;
    }


}