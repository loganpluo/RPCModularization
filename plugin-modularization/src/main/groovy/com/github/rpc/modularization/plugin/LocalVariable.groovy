package com.github.rpc.modularization.plugin
/**
 * label不同的话还是支持不了
 * 暂时支持label 从最开始 到最后的情况
 */
class LocalVariable {

    public String name
    public String desc

    public LocalVariable(String name, String desc){
        this.name = name
        this.desc = desc
    }

    @Override
    String toString() {
        return "name:$name, desc:$desc"
    }
}