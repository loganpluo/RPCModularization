package com.github.rpc.modularization.plugin

class CodeGeneratorUtil{

    static final String TAG = "CodeGeneratorUtil"

    //android/content/Context;com/github/rpc/modularization/RPCModule
    //(Landroid/content/Context;Lcom/github/rpc/modularization/RPCModule;)V
    static String getSignCallMethodParams(String callMethodParamsConfig){

        StringBuilder stringBuilder = new StringBuilder()
        stringBuilder.append("(")

        String[]  callMethodParam = callMethodParamsConfig.split(";")
        callMethodParam.each {
            stringBuilder.append("L$it;")
        }

        stringBuilder.append(")V")

        LogUtil.i(TAG,"getSignCallMethodParams ${stringBuilder.toString()}")

        return stringBuilder.toString()
    }

}