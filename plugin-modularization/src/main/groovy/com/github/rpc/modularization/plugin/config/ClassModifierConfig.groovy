package com.github.rpc.modularization.plugin.config


import java.util.regex.Pattern

/**
 * 替换了/ 的路径了
 */
class ClassModifierConfig {

    String type;//对应ClassModifierType.type
    String scanInterface;
    String scanAnnotation;
    String codeInsertToClass;
    String codeInsertToMethod;
    String codeInsertToMethodParams;
    String callMethodName;
    String callMethodParams;
    List<LocalVariable> localVariables;
    ArrayList<Pattern> includePatterns = []
    ArrayList<Pattern> excludePatterns = []

    @Override
    String toString() {
        StringBuilder sb = new StringBuilder('{')
        sb.append('\n\t').append('type').append('\t\t\t=\t').append(type)
        sb.append('\n\t').append('scanInterface').append('\t\t\t=\t').append(scanInterface)
        sb.append('\n\t').append('scanAnnotation').append('\t\t\t=\t').append(scanAnnotation)
        sb.append('\n\t').append('codeInsertToClass').append('\t\t\t=\t').append(codeInsertToClass)
        sb.append('\n\t').append('codeInsertToMethod').append('\t\t\t=\t').append(codeInsertToMethod)
        sb.append('\n\t').append('codeInsertToMethodParams').append('\t\t\t=\t').append(codeInsertToMethodParams)
        sb.append('\n\t').append('callMethodName').append('\t\t\t=\t').append(callMethodName)
        sb.append('\n\t').append('callMethodParams').append('\t\t\t=\t').append(callMethodParams)
        sb.append('\n\t').append('localVariables').append('\t\t\t=\t').append(localVariables)
        sb.append('\n\t').append('excludePatterns').append('\t\t\t=\t').append(excludePatterns)
        return sb.toString()
    }
}