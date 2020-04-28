package com.github.rpc.modularization.plugin

import org.objectweb.asm.MethodVisitor

abstract class ClassModifier {

    ClassModifierConfig classModifierConfig = new ClassModifierConfig()

    File codeInsertToClassFile;
    Set<String> classList = new HashSet<>();

    abstract boolean isTargetCodeInsertToClassFile(File file)

    abstract boolean isSetClassFile(File file)

    /**
     * 记录被修改的 codeInsertToClassFile
     * 被注册的类 classList
     */
    abstract boolean recordClassModifierTarget(String destFile,
                                            int version, int access, String name,
                                            String signature, String superName, String[] interfaces)



    abstract void parse(Map<String, Object> config)
    abstract ICodeGenerator getICodeGenerator()

    static String convertDotToSlash(String str) {
        return str ? str.replaceAll('\\.', '/').intern() : str
    }


}