package com.github.rpc.modularization.plugin

enum ClassModifyType{
    InterfaceModuleInit("InterfaceModuleInit"),//模块自动注册
    AnnotationModuleAutoRegister("AnnotationModuleAutoRegister"),//注解模块自动注册
    public String type
    public ClassModifyType(String type){
        this.type = type
    }

    public String getValue(){
        return type
    }

}