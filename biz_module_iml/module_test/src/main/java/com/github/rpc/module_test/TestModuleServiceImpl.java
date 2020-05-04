package com.github.rpc.module_test;

import com.github.rpc.modularization.ModuleService;
import com.github.rpc.module_login_api.LoginModuleService;
import com.github.rpc.module_login_api.Session;

@ModuleService
public class TestModuleServiceImpl implements TestModuleService  {
    @Override
    public TestBean getBean() {
        return new TestBean("TestBean");
    }//
//    @Override
//    public boolean isLogin() {
//        return true;
//    }
//
//    @Override
//    public Session getCurrentSession() {
//        return null;
//    }
}
