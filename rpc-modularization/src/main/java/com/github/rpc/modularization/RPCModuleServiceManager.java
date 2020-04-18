package com.github.rpc.modularization;

import java.util.HashMap;
import java.util.Map;

public class RPCModuleServiceManager {

    private static class RPCServiceManagerHolder {
        private static final RPCModuleServiceManager INSTANCE = new RPCModuleServiceManager();
    }

    public static RPCModuleServiceManager getInstance() {
        return RPCServiceManagerHolder.INSTANCE;
    }

    private Map<String, Class<? extends RPCModuleService>> allServicesDictionary;
    private Map<String, RPCModuleService> allInstanceDictionary;

    private RPCModuleServiceManager() {
        allServicesDictionary = new HashMap<>();
        allInstanceDictionary = new HashMap<>();
    }


    /**
     * 注册服务实例
     *
     * @param service  实例协议描述
     * @param instance 服务实例对象
     */
    public void registerService(Class<? extends RPCModuleService> service, RPCModuleService instance) {
        allInstanceDictionary.put(service.getSimpleName(), instance);
    }

    /**
     * 注册服务实例类，RPCServiceManager#findService会利用该类创建服务实例
     *
     * @param service   实例协议描述
     * @param implClass 服务实例类
     */
    public void registerService(Class<? extends RPCModuleService> service, Class<? extends RPCModuleService> implClass) {
        allServicesDictionary.put(service.getSimpleName(), implClass);
    }

    /**
     * 反注册服务，会同时清空已登记的实现类与已缓存的实例对象
     *
     * @param service 实例协议描述
     */
    public void unregisterService(Class<? extends RPCModuleService> service) {
        allInstanceDictionary.remove(service.getSimpleName());
        allServicesDictionary.remove(service.getSimpleName());
    }

    /**
     * 方便的服务查找接口，不用先调用{@link RPCModuleServiceManager#getInstance}取得实例，参数具体描述见{@link RPCModuleServiceManager#findService}
     *
     * @param <ModuleService> 服务协议类型
     * @param service           实例协议描述
     * @return 创建的服务实例或者nil
     */
    public static <ModuleService extends RPCModuleService> ModuleService findService(Class<ModuleService> service) {
        return RPCModuleServiceManager.getInstance().innerFindService(service);
    }


    private <ModuleService extends RPCModuleService> ModuleService innerFindService(Class<ModuleService> service) {

        String serviceName = service.getSimpleName();
        RPCModuleService serviceInstance = allInstanceDictionary.get(serviceName);
        if (serviceInstance != null) {
            return (ModuleService) serviceInstance;
        }

        Class<? extends RPCModuleService> serviceImpl = allServicesDictionary.get(serviceName);
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
