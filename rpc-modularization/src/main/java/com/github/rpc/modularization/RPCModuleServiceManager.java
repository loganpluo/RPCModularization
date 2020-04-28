package com.github.rpc.modularization;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class RPCModuleServiceManager {

    private static final String TAG = "RPCModuleServiceManager";

    private static class RPCServiceManagerHolder {
        private static final RPCModuleServiceManager INSTANCE = new RPCModuleServiceManager();
    }

    public static RPCModuleServiceManager getInstance() {
        return RPCServiceManagerHolder.INSTANCE;
    }

    private Map<String, Class<?>> allServicesDictionary;
    private Map<String, Object> allInstanceDictionary;

    private RPCModuleServiceManager() {
        allServicesDictionary = new HashMap<>();
        allInstanceDictionary = new HashMap<>();
    }

    public static void init(Context context){
        initModules(context);
        initModuleServices();
    }

    public static void initModules(Context context){
        //initModule(context,new LoginModuleImpl());
        //
    }

    public static void initModuleServices(){
        //registerModuleService(LoginModuleServiceImpl.class);
    }

    private static void initModule(Context context, RPCModule rpcModule){
        rpcModule.onInit(context);
    }

    private static void registerModuleService(Class<?> serviceImpl){
        Class<?>[] serviceInterfaces = serviceImpl.getInterfaces();
        getInstance().registerModuleService(serviceInterfaces[0], serviceImpl);
        Log.i(TAG,"registerModuleService serviceImpl:"+serviceImpl.getName());
    }

    private void registerModuleService(Class serviceApiClass, Class<?> serviceImplClass){
        allInstanceDictionary.put(getServiceKey(serviceApiClass), serviceImplClass);
    }

    private String getServiceKey(Class serviceApiClass){
        return serviceApiClass.getName();
    }

    /**
     * 注册服务实例
     *
     * @param service  实例协议描述
     * @param instance 服务实例对象
     */
    public void registerService(Class<?> service, Object instance) {
        allInstanceDictionary.put(getServiceKey(service), instance);
    }

    /**
     * 注册服务实例类，RPCServiceManager#findService会利用该类创建服务实例
     *
     * @param service   实例协议描述
     * @param implClass 服务实例类
     */
    public void registerService(Class<? extends Object> service, Class<? extends Object> implClass) {
        allServicesDictionary.put(getServiceKey(service), implClass);
    }

    /**
     * 反注册服务，会同时清空已登记的实现类与已缓存的实例对象
     *
     * @param service 实例协议描述
     */
    public void unregisterService(Class<? extends Object> service) {
        allInstanceDictionary.remove(getServiceKey(service));
        allServicesDictionary.remove(getServiceKey(service));
    }

    /**
     * 方便的服务查找接口，不用先调用{@link RPCModuleServiceManager#getInstance}取得实例，参数具体描述见{@link RPCModuleServiceManager#findService}
     *
     * @param <ModuleService> 服务协议类型
     * @param service           实例协议描述
     * @return 创建的服务实例或者nil
     */
    public static <ModuleService extends Object> ModuleService findService(Class<ModuleService> service) {
        return RPCModuleServiceManager.getInstance().innerFindService(service);
    }


    private <ModuleService extends Object> ModuleService innerFindService(Class<ModuleService> service) {

        String serviceName = getServiceKey(service);
        Object serviceInstance = allInstanceDictionary.get(serviceName);
        if (serviceInstance != null) {
            return (ModuleService) serviceInstance;
        }

        Class<? extends Object> serviceImpl = allServicesDictionary.get(serviceName);
        if (serviceImpl == null) {
            return null;
        }


        try {
            serviceInstance = serviceImpl.newInstance();
            return (ModuleService) serviceInstance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
