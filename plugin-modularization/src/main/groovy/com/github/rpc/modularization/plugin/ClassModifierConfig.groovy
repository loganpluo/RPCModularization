package com.github.rpc.modularization.plugin

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
    ArrayList<String> exclude;

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
        sb.append('\n\t').append('exclude').append('\t\t\t=\t').append(exclude)
        return sb.toString()
    }
}