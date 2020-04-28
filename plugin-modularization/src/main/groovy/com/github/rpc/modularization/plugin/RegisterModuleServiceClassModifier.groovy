package com.github.rpc.modularization.plugin

class RegisterModuleServiceClassModifier extends ClassModifier {


    @Override
    boolean isTargetCodeInsertToClassFile(File file) {
        return false
    }

    @Override
    boolean isSetClassFile(File file){
        return false
    }

    @Override
    boolean recordClassModifierTarget(String destFile,
                                   int version, int access, String name,
                                   String signature, String superName, String[] interfaces) {
        def result = false

        return result
    }

    @Override
    void parse(Map<String, Object> config) {

    }

    @Override
    ICodeGenerator getICodeGenerator() {
        return null
    }
}