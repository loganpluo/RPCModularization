package com.github.rpc.modularization.plugin.util

class LogUtil{

    static boolean isDebugPMLog = false

    static void d(String TAG, String msg){
        if(isDebugPMLog){
            println(" $TAG : $msg")
        }
    }

    static void i(String TAG, String msg){
        println(" $TAG : $msg")
    }

}