package com.github.rpc.modularization.plugin

class ClassModifyInfo {

    ClassModifyType type;
    String scanInterface;
    String scanAnnotation;
    String codeInsertToClass;
    String codeInsertToMethod;
    String codeInsertToMethodParams;
    String callMethodName;
    String callMethodParams;
    ArrayList<String> exclude;

    File codeInsertToClassFile;
    Set<String> classList;

    ICodeGenerator codeGenerator;

}