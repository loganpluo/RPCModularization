package com.github.rpc.modularization.plugin

abstract class ClassModifier {

    ClassModifierConfig ClassModifierConfig

    File codeInsertToClassFile;
    Set<String> classList;

    ICodeGenerator codeGenerator;

    abstract ICodeGenerator getICodeGenerator()

}