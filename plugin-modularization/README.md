### 插件实现的功能


### 插件工程结构
```
src/main/groovy/com.github.rpc.modularization.plugin
    ModularizationPlugin.groovy --插件入口类
    InjectTransform.groovy -- transform api 
    ClassModifierExtension -- 获取到modularization.gradle的配置 转成到 classModifiers
    ScanHelper.groovy -- 扫描 目录、jar、 class 类，调用classModifiers
    InitModuleClassModifier.groovy -- 模块初始化字节码修改处理类，得到被修改的class 和 需要注入的class
    InitModuleCodeGenerator.groovy -- 模块初始化字节码插入代码类，把需要注入的class，到被修改的class的方法里面
    RegisterModuleServiceClassModifier.groovy -- 模块接口服务绑定字节码修改处理类，得到被修改的class 和 需要注入的class
    RegisterModuleServiceCodeGenerator.groovy -- 模模块接口服务绑定字节码插入代码处理类，把需要注入的class，到被修改的class的方法里面
```

### 插件开发
```
step1 根目录 build.gradle plugin-modularization插件修改成本地仓库引用

    repositories {
        maven {//本地Maven仓库地址找自定义的插件 todo 后面放到远程maven
            url uri('repos')
        }
    }

step2 plugin-modularization插件发布地址改成本地 build.gradle， 方便频繁upload进行调试

    uploadArchives {
        repositories {
            mavenDeployer {
               repository(url: uri('../repos'))
            }
        }
    }//
    
step3 直接查看目标 java文件的字节码 as的插件
https://plugins.jetbrains.com/plugin/5918-asm-bytecode-outline   

step4 asm编写修改字节码的指令，api和最后字节码结构一一对应
例如 RegisterModuleServiceCodeGenerator 
可参考指引：https://blog.csdn.net/ouyang_peng/article/details/100566678

java类型签名 https://www.jianshu.com/p/a1438b476e82

step5 build里面可以查看 执行 plugin-modularization的log （warn： 有时候又查看不到，clean又不行好奇怪）

step6 反编译apk里面的dex 成class


step7 查看 编译的class

javap -c D:\Hello.class
其他方式
    https://blog.csdn.net/kwame211/article/details/77677662
    http://set.ee/jbe/

step8 有问题的化，比对编译出来的class 和目标的class结构，可以看出


### 需要优化的部分
* 编译速度
* 代码写死配置，再优化下


```    
