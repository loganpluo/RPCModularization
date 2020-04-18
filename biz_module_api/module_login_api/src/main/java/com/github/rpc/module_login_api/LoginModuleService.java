package com.github.rpc.module_login_api;

import com.github.rpc.modularization.RPCModuleService;

public interface LoginModuleService extends RPCModuleService {

    Session getCurrentSession();

}
