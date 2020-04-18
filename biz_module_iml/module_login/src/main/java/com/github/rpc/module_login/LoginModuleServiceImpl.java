package com.github.rpc.module_login;

import com.github.rpc.module_login_api.LoginModuleService;
import com.github.rpc.module_login_api.Session;

public class LoginModuleServiceImpl implements LoginModuleService {
    @Override
    public boolean isLogin() {
        return true;
    }

    @Override
    public Session getCurrentSession() {
        return null;
    }
}
