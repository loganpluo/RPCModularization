package com.github.rpc.modularization.plugin.class_modifier

import com.github.rpc.modularization.plugin.incremental_compile.InitModuleScanCacheService
import com.github.rpc.modularization.plugin.incremental_compile.IScanResultCacheService
import com.github.rpc.modularization.plugin.incremental_compile.RegisterModuleScanCacheService

enum ClassModifierType {
    InterfaceModuleInit("InterfaceModuleInit",
                         InitModuleClassModifier.class,
                         InitModuleScanCacheService.class),//模块自动初始化
    AnnotationModuleAutoRegister("AnnotationModuleAutoRegister",
                                 RegisterModuleServiceClassModifier.class,
            RegisterModuleScanCacheService.class)//注解模块自动注册

    public String type
    public Class<? extends ClassModifier> classModifyClass
    public Class<? extends IScanResultCacheService> scanResultCacheServiceClass
    public ClassModifierType(String type,
                             Class<? extends ClassModifier> classModifyClass,
                             Class<? extends IScanResultCacheService> scanResultCacheServiceClass){
        this.type = type
        this.classModifyClass = classModifyClass;
        this.scanResultCacheServiceClass = scanResultCacheServiceClass
    }

    String getType() {
        return type
    }

    Class<? extends ClassModifier> getClassModifyClass() {
        return classModifyClass
    }

    Class<? extends IScanResultCacheService> getScanResultCacheServiceClass() {
        return scanResultCacheServiceClass
    }
}