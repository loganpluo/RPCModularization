### 插件实现的功能
```
//扫描如下类

@ModuleService
public class TopicModuleServiceImpl implements TopicModuleService {

}

public class TopicModule implements RPCModule {
    @Override
    public void onInit(Context context) {
        
    }
}

// 插件收利用transform接口 集到 上面的注解@ModuleService类、 继承RPCModule接口的类， 
// 利用asm 注册到RPCModuleServiceManager.java的占位的方法，如下
public class RPCModuleServiceManager {
    public static void initModules(Context context){//占位的方法
        initModule(context,new LoginModule());// asm修改注入的code，实现模块初始化
    }

    public static void initModuleServices(){
        registerModuleService(LoginModuleServiceImpl.class);// asm修改注入的code，实现接口 和 实现类的绑定
    }    
}

```

### 插件工程关键类
```
src/main/groovy/com.github.rpc.modularization.plugin
    ModularizationPlugin.groovy --插件入口类
    InjectTransform.groovy -- transform api 拦截到 class 打包成dex之前
    ClassModifierExtension -- 获取到modularization.gradle的配置 转成 classModifiers
    ScanHelper.groovy -- 扫描 目录、jar、 class 类，调用classModifiers
    
    ClassModifier -- 字节码修改功能抽象类，抽象方法有 配置解析、扫描记录目标类信息、字节码修改，方便后续扩展支持新配置修改字节码；
    InitModuleClassModifier.groovy -- 模块初始化字节码修改处理类，得到被修改的class 和 需要注入的class
    InitModuleCodeGenerator.groovy -- 模块初始化字节码插入代码类，把需要注入的class，到被修改的class的方法里面
    RegisterModuleServiceClassModifier.groovy -- 模块接口服务绑定字节码修改处理类，得到被修改的class 和 需要注入的class
    RegisterModuleServiceCodeGenerator.groovy -- 模模块接口服务绑定字节码插入代码处理类，把需要注入的class，到被修改的class的方法里面
    
    ApiDirHelper.groovy -- 自动识别 gradle.properties配置的module_iml_api_src的.api目录为src目录
    
    IScanResultCacheService.groovy -- 增量编译 应用缓存扫描结果接口定义
    InitModuleScanCacheService.groovy -- 自动初始化模块字节码修改 增量编译扫描实现
    RegisterModuleScanCacheService.groovy -- 自动注册模块字节码修改 增量编译扫描实现
```


### 插件开发
#### step1 根目录 build.gradle plugin-modularization插件修改成本地仓库引用
```
    repositories {
        maven {//本地Maven仓库地址找自定义的插件 
            url uri('repos')
        }
    }
```

#### step2 plugin-modularization插件发布地址改成本地 build.gradle， 方便频繁upload进行调试
```
    uploadArchives {
        repositories {
            mavenDeployer {
               repository(url: uri('../repos'))
            }
        }
    }//
```

#### step3 直接查看目标 java文件的字节码 as的插件
https://plugins.jetbrains.com/plugin/5918-asm-bytecode-outline   

#### step4 asm编写修改字节码的指令，api和最后字节码结构一一对应
例如 RegisterModuleServiceCodeGenerator  <br>
可参考指引：https://blog.csdn.net/ouyang_peng/article/details/100566678 <br>

java类型签名 https://www.jianshu.com/p/a1438b476e82 <br>

#### step5 build里面可以查看 执行 plugin-modularization的log （warn： 有时候又查看不到，clean又不行好奇怪）

#### step6 反编译apk里面的dex 成class
![](https://github.com/loganpluo/RPCModularization/blob/master/pic/asm.png)<br>

#### step7 查看 编译的class

javap -c D:\Hello.class<br>
其他方式<br>
    https://blog.csdn.net/kwame211/article/details/77677662 <br>
    http://set.ee/jbe/<br>

#### step8 有问题的化，比对编译出来的class 和目标的class结构，可以看出

#### step9 插件增量编译支持配置gradle.properties

```
##使用增量编译缓存
enableIncrementalCache=true
```

#### step10 插件编译过滤配置,正则表达式
* modularization.gradle include 和 exclude属性配置，注意匹配的是包名是/分隔符； 
```
apply plugin: 'com.github.rpc.modularization'


classmodifier{
    configs = [
        [ //自动注册组件
          'type'             : 'InterfaceModuleInit',
          'scanInterface'             : 'com.github.rpc.modularization.RPCModule'
          , 'codeInsertToClass'   : 'com.github.rpc.modularization.RPCModuleServiceManager'
          , 'codeInsertToMethod'      : 'initModules'
          , 'codeInsertToMethodParams': 'android.content.Context'
          , 'codeInsertToMethodLocalVariables':[//asm 站桩方法的变量定义
                                        ['name':'context', 'type': 'android.content.Context']
          ]
          , 'callMethodName'      : 'initModule'
          , 'callMethodParams': 'android.content.Context;com.github.rpc.modularization.RPCModule'
//          , 'include'                 : [//包含类，支持正则表达式（包分隔符需要用/表示，不能用.）
//                                         'com.github.rpc'.replaceAll("\\.", "/") + ".*",
//           ]
          , 'exclude'                 : [//排除的类，支持正则表达式（包分隔符需要用/表示，不能用.）
                 'androidx.'.replaceAll("\\.", "/") + ".*",
                  'android.support'.replaceAll("\\.", "/") + ".*"
            ]
        ],
        [ //自动注册组件
          'type'             : 'AnnotationModuleAutoRegister',
          'scanAnnotation'   : 'com.github.rpc.modularization.ModuleService'
          , 'codeInsertToClass'   : 'com.github.rpc.modularization.RPCModuleServiceManager'
          , 'codeInsertToMethod'      : 'initModuleServices'
          , 'callMethodName'      : 'registerService'
          , 'callMethodParams': 'java.lang.Class'
//          , 'include'                 : [//包含类，支持正则表达式（包分隔符需要用/表示，不能用.）
//                                         'com.github.rpc'.replaceAll("\\.", "/") + ".*",
//            ]
          , 'exclude'                 : [//排除的类，支持正则表达式（包分隔符需要用/表示，不能用.）
                 'androidx.'.replaceAll("\\.", "/") + ".*",
                  'android.support'.replaceAll("\\.", "/") + ".*"
            ]
        ]
    ]
}
```


