package com.github.rpc.module_login_api;

public interface LoginModuleService {
    String getUserName();
    boolean isLogin();
    Session getCurrentSession();

}
