package com.github.rpc.modularization.plugin.util

class LogUtil{

    static boolean enableDebugPMLog = false

    static void d(String TAG, String msg){
        if(enableDebugPMLog){
            println(" $TAG : $msg")
        }
    }

    static void i(String TAG, String msg){
        println(" $TAG : $msg")
    }

}