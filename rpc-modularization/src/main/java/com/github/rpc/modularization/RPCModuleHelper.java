package com.github.rpc.modularization;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Constructor;

public class RPCModuleHelper {

    private static String TAG = "RPCModuleHelper";

    public static void newInstanceAndInit(Context context,String className) {
        RPCModule module =  newInstance(className);
        if(module != null){
            module.onInit(context);
            Log.i(TAG,"newInstanceAndInit success for "+className);
        }else{
            Log.e(TAG,"newInstanceAndInit failed for "+className);
        }
    }

    public static <T extends RPCModule> T newInstance(String className) {
        return newInstance(className, new Class[]{}, new Object[]{});
    }

    public static <T extends RPCModule> T newInstance(String className, Class<?>[] argClass, Object... param) {
        try {
            Class clazz  = Class.forName(className);
            Log.d(TAG, "clazz found:" + clazz);

            Constructor constructor = clazz.getConstructor(argClass);
            return (T) constructor.newInstance(param);
        } catch (Exception e) {
            Log.e(TAG, "Utils.getObject class"  + className + "  error msg:" + e);
        }
        return  null;
    }

}
