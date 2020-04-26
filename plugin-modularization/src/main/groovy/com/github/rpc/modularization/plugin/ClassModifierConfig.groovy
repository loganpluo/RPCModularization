package com.github.rpc.modularization.plugin

class ClassModifierConfig {

    String type;//对应ClassModifierType.type
    String scanInterface;
    String scanAnnotation;
    String codeInsertToClass;
    String codeInsertToMethod;
    String codeInsertToMethodParams;
    String callMethodName;
    String callMethodParams;
    ArrayList<String> exclude;

}