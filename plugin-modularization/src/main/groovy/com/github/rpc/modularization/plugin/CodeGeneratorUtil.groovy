package com.github.rpc.modularization.plugin

class CodeGeneratorUtil{

    static final String TAG = "CodeGeneratorUtil"

    //android/content/Context;com/github/rpc/modularization/RPCModule
    //(Landroid/content/Context;Lcom/github/rpc/modularization/RPCModule;)V
    /**
     * 暂时这么改了，因为之前逻辑都是配置里面只进行.替换/， 修改代码在加签名前缀什么的
     * @param callMethodParamsConfig
     * @return
     */
    static String getDescCallMethodParams(String callMethodParamsConfig){

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