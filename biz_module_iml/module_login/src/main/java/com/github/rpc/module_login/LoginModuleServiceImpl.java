package com.github.rpc.module_login;

import com.github.rpc.modularization.ModuleService;
import com.github.rpc.module_login_api.LoginModuleService;
import com.github.rpc.module_login_api.Session;

@ModuleService
public class LoginModuleServiceImpl implements LoginModuleService {
    @Override
    public String getUserName() {
        return "loganpluo";
    }

    @Override
    public boolean isLogin() {
        return true;
    }

    @Override
    public Session getCurrentSession() {
        return null;
    }
}
