package com.github.rpc.modularization.plugin.incremental_compile


class InitModuleScanCacheService extends CommScanCacheService {


    @Override
    String getCacheFileName() {
        return "InitModule.json"
    }

    @Override
    String getTag() {
        return "InitModuleScanCacheService"
    }




}