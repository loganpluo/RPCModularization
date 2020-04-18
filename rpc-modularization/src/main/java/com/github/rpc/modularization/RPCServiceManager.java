package com.github.rpc.modularization;

import java.util.HashMap;
import java.util.Map;

public class RPCServiceManager {

    private static class RPCServiceManagerHolder {
        private static final RPCServiceManager INSTANCE = new RPCServiceManager();
    }

    public static  RPCServiceManager getInstance() {
        return RPCServiceManagerHolder.INSTANCE;
    }

    private Map<String, Class<? extends RPCServiceInterface>> allServicesDictionary;
    private Map<String, RPCServiceInterface> allInstanceDictionary;

    private RPCServiceManager() {
        allServicesDictionary = new HashMap<>();
        allInstanceDictionary = new HashMap<>();
    }


    /**
     * 注册服务实例
     *
     * @param service  实例协议描述
     * @param instance 服务实例对象
     */
    public void registerService(Class<? extends RPCServiceInterface> service, RPCServiceInterface instance) {
        allInstanceDictionary.put(service.getSimpleName(), instance);
    }

    /**
     * 注册服务实例类，RPCServiceManager#findService会利用该类创建服务实例
     *
     * @param service   实例协议描述
     * @param implClass 服务实例类
     */
    public void registerService(Class<? extends RPCServiceInterface> service, Class<? extends RPCServiceInterface> implClass) {
        allServicesDictionary.put(service.getSimpleName(), implClass);
    }

    /**
     * 反注册服务，会同时清空已登记的实现类与已缓存的实例对象
     *
     * @param service 实例协议描述
     */
    public void unregisterService(Class<? extends RPCServiceInterface> service) {
        allInstanceDictionary.remove(service.getSimpleName());
        allServicesDictionary.remove(service.getSimpleName());
    }

    /**
     * 方便的服务查找接口，不用先调用{@link RPCServiceManager#getInstance}取得实例，参数具体描述见{@link RPCServiceManager#findService}
     *
     * @param <ServiceProtocol> 服务协议类型
     * @param service           实例协议描述
     * @return 创建的服务实例或者nil
     */
    public static <ServiceProtocol extends RPCServiceInterface> ServiceProtocol findService(Class<ServiceProtocol> service) {
        return RPCServiceManager.getInstance().innerFindService(service);
    }


    private <ServiceProtocol extends RPCServiceInterface> ServiceProtocol innerFindService(Class<ServiceProtocol> service) {

        String serviceName = service.getSimpleName();
        RPCServiceInterface serviceInstance = allInstanceDictionary.get(serviceName);
        if (serviceInstance != null) {
            return (ServiceProtocol) serviceInstance;
        }

        Class<? extends RPCServiceInterface> serviceImpl = allServicesDictionary.get(serviceName);
        if (serviceImpl == null) {
            return null;
        }


        try {
            serviceInstance = serviceImpl.newInstance();
            return (ServiceProtocol) serviceInstance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
