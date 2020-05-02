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

    private Map<String, Class<?>> allApiServicesDictionary;// 服务接口class.name - 实现服务的class映射表
    private Map<String, Object> allSingleInstanceDictionary;// 全局单利服务

    private RPCModuleServiceManager() {
        allApiServicesDictionary = new HashMap<>();
        allSingleInstanceDictionary = new HashMap<>();
    }

    public static void init(Context context){
        initModules(context);
        initModuleServices();
    }

    public static void initModules(Context context){
    }

    public static void initModuleServices(){
    }

    private static void initModule(Context context, RPCModule rpcModule){
        rpcModule.onInit(context);
    }

    private static void registerModuleService(Class<?> serviceImpl){
        Class<?>[] serviceInterfaces = serviceImpl.getInterfaces();
        if(serviceInterfaces.length == 0 ){
            throw new ArrayIndexOutOfBoundsException("@ModuleService "+serviceImpl.getName()
                    +" must implements module api interface");
        }
        getInstance().registerModuleService(serviceInterfaces[0], serviceImpl);
        Log.i(TAG,"registerModuleService serviceImpl:"+serviceImpl.getName());
    }

    private void registerModuleService(Class serviceApiClass, Class<?> serviceImplClass){
        allApiServicesDictionary.put(getServiceKey(serviceApiClass), serviceImplClass);
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
        allSingleInstanceDictionary.put(getServiceKey(service), instance);
    }

    /**
     * 注册服务实例类，RPCServiceManager#findService会利用该类创建服务实例
     *
     * @param service   实例协议描述
     * @param implClass 服务实例类
     */
    public void registerService(Class<?> service, Class<?> implClass) {
        allApiServicesDictionary.put(getServiceKey(service), implClass);
    }

    /**
     * 反注册服务，会同时清空已登记的实现类与已缓存的实例对象
     *
     * @param service 实例协议描述
     */
    public void unregisterService(Class<?> service) {
        allSingleInstanceDictionary.remove(getServiceKey(service));
        allApiServicesDictionary.remove(getServiceKey(service));
    }

    public void unregisterSingleInstanceService(Class<?> service) {
        allSingleInstanceDictionary.remove(getServiceKey(service));
    }

    public static <ModuleService> ModuleService findService(Class<ModuleService> service) {
        return findService(service, ModuleServiceType.New);
    }


    public static <ModuleService> ModuleService findService(Class<ModuleService> service,
                                                                           ModuleServiceType moduleServiceType) {
        return RPCModuleServiceManager.getInstance().innerFindService(service, moduleServiceType);
    }

    public  <ModuleService> ModuleService innerFindService(Class<ModuleService> service,
                                                           ModuleServiceType moduleServiceType) {
        if(moduleServiceType == ModuleServiceType.SingleInstance){
            return RPCModuleServiceManager.getInstance().innerFindSingleInstanceService(service);
        }

        return RPCModuleServiceManager.getInstance().innerFindSingleInstanceService(service);
    }

    private <ModuleService> ModuleService innerFindSingleInstanceService(Class<ModuleService> service) {

        String serviceName = getServiceKey(service);
        Object serviceInstance = allSingleInstanceDictionary.get(serviceName);
        if (serviceInstance != null) {
            return (ModuleService) serviceInstance;
        }

        Object serviceImpl =  innerFindNewService(service);
        if(serviceImpl != null){
            allSingleInstanceDictionary.put(serviceName,serviceImpl);
        }
        return (ModuleService) serviceImpl;
    }

    private <ModuleService> ModuleService innerFindNewService(Class<ModuleService> service) {

        String serviceName = getServiceKey(service);
        Class<?> serviceImpl = allApiServicesDictionary.get(serviceName);
        if (serviceImpl == null) {
            return null;
        }


        try {
            return (ModuleService) serviceImpl.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
