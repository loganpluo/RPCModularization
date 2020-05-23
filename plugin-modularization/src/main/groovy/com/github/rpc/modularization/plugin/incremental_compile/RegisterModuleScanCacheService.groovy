package com.github.rpc.modularization.plugin.incremental_compile


class RegisterModuleScanCacheService extends CommScanCacheService {


    @Override
    String getCacheFileName() {
        return "RegisterModule.json"
    }

    @Override
    String getTag() {
        return "RegisterModuleScanCacheService"
    }
}