package com.github.rpc.modularization;

import android.content.Context;

public class AppInitModularization {

    public static void init(Context context){
        initModule(context);
        initModuleService();
        //反射newapt生成的类的 调用 initModule 和 initModuleService方法

        //累吗
    }

    private static void initModule(Context context){

    }

    private static void initModuleService(){

    }

}
