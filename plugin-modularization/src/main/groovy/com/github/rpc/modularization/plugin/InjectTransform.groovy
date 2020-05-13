package com.github.rpc.modularization.plugin

import com.android.build.api.transform.Context
import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Status
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.github.rpc.modularization.plugin.class_modifier.ClassModifierType
import com.github.rpc.modularization.plugin.code_generator.InsertCodeHelper
import com.github.rpc.modularization.plugin.config.ClassModifierExtension
import com.github.rpc.modularization.plugin.scan.ScanHelper
import com.github.rpc.modularization.plugin.util.LogUtil
import org.gradle.api.Project
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import com.android.build.api.transform.Format
import org.apache.commons.io.FileUtils

/**
 * 定义一个Transform
 */
class InjectTransform extends Transform {

    private static String TAG = "InjectTransform"

    private Project mProject
    ClassModifierExtension extension;

    // 构造函数，我们将Project保存下来备用
    InjectTransform(Project project) {
        this.mProject = project
    }

    // 设置我们自定义的Transform对应的Task名称
    // 类似：transformClassesWithPreDexForXXX
    // 这里应该是：transformClassesWithInjectTransformForxxx
    @Override
    String getName() {
        return 'InjectTransform'
    }

    // 指定输入的类型，通过这里的设定，可以指定我们要处理的文件类型
    //  这样确保其他类型的文件不会传入
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    // 指定Transform的作用范围
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    // 当前Transform是否支持增量编译
    @Override
    boolean isIncremental() {
        return true
    }

    // 核心方法
    // inputs是传过来的输入流，有两种格式：jar和目录格式
    // outputProvider 获取输出目录，将修改的文件复制到输出目录，必须执行
    @Override
    void transform(Context context, Collection<TransformInput> inputs,
                   Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider,
                   boolean isIncremental) throws IOException, TransformException, InterruptedException {
        def startTs = System.currentTimeMillis()
        extension.loadConfig()
        println "-------------------- ${getName()} transform 开始 isIncremental：${isIncremental}, " +
                "isCacheable:${isCacheable()}-------------------"

        if("true" == mProject.getProperties().get("isDebugPMLog")){
            LogUtil.isDebugPMLog = true
            println("isDebugPMLog true")
        }

        println "inputs.size: ${inputs.size()} ,ClassModifyType:${ClassModifierType.InterfaceModuleInit.type}"

        LogUtil.i(TAG,"classModifiers: ${extension.classModifiers}")

        // Transform的inputs有两种类型，一种是目录，一种是jar包，要分开遍历
        inputs.each {
            TransformInput input ->
                // 遍历文件夹
                //文件夹里面包含的是我们手写的类以及R.class、BuildConfig.class以及R$XXX.class等
                scanDirectoryInputs(isIncremental,
                                    outputProvider,
                                    input.directoryInputs)

                //对类型为jar文件的input进行遍历
                scanJarInputs(isIncremental,
                              outputProvider,
                              input.jarInputs)
        }

        //根据扫描收集到信息 进行 class的修改
        extension.classModifiers.each {
            if(it.hasWholeInfo()){
                InsertCodeHelper.insertInitCodeTo(it)
            }else{
                LogUtil.i(TAG,"classModifiers codeInsertToClassFile:${it.codeInsertToClassFile}, " +
                        " classList.isEmpty:${it.classList.isEmpty()}")
            }
        }

        long cost = System.currentTimeMillis() - startTs
        println "--------------------- ${getName()} transform cost:$cost 结束-------------------"
    }

    /**
     * 扫描包含class的目录
     * 文件夹里面包含的是我们手写的类以及R.class、BuildConfig.class以及R$XXX.class等
     * @param directoryInputs
     */
    void scanDirectoryInputs(boolean isIncremental,
                             TransformOutputProvider outputProvider,
                             Collection<DirectoryInput> directoryInputs){
        LogUtil.i(TAG,"scanDirectory.changedFiles.isEmpty: ${directoryInputs.changedFiles.isEmpty()}")
        LogUtil.d(TAG,"input.directoryInputs.size:${directoryInputs.size()}")

        for(DirectoryInput directoryInput : directoryInputs){

            // 注入代码
            LogUtil.d(TAG,"scanDirectory path:${directoryInput.file.absolutePath} " +
                    "isIncremental：$isIncremental "+
                    "directoryInput.changedFiles.size:${directoryInput.changedFiles.size()} ")

            //增量编译 目录没有变化则无需copy
//            if(isIncremental && directoryInput.changedFiles.isEmpty()){
//                continue
//            }

            // 获取输出目录
            def dest = outputProvider.getContentLocation(directoryInput.name,
                    directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
            String root = directoryInput.file.absolutePath
            if (!root.endsWith(File.separator)) root += File.separator

            LogUtil.d(TAG,"scanDirectory root:$root")

            //增量编译 有变化 则只需要读取变化的文件修改class
            if(isIncremental && !directoryInput.changedFiles.isEmpty()){
                LogUtil.d(TAG,"scanDirectory changedFile:${it}")
                directoryInput.changedFiles.each { fileList ->
                    def file = fileList.key
                    if (fileList.value == Status.CHANGED || fileList.value == Status.ADDED) {
                        ScanHelper.scanClassFile(file,
                                root, extension.classModifiers,
                                dest)
                    }
                }
            }else{
                ScanHelper.scanDirectory(directoryInput.file.absolutePath,
                        root, extension.classModifiers,
                        dest)
            }

            // 将input的目录复制到output指定目录
            FileUtils.copyDirectory(directoryInput.file, dest)
        }
    }

    /**
     * 扫描jars包（library / 第三方 jar包）
     */
    void scanJarInputs(boolean isIncremental,
                       TransformOutputProvider outputProvider,
                       Collection<JarInput> jarInputs){
        LogUtil.d(TAG,"input.jarInputs.size:${jarInputs.size()}")
        for(JarInput jarInput : jarInputs){

            // 重命名输出文件（同目录copyFile会冲突）
            def jarName = jarInput.name
            println("jar name: $jarInput.name, isIncremental：$isIncremental jarInput.status: ${jarInput.status}")

            //增量编译 没有变化的返回NOTCHANGED, 则无需copy处理
//            if(isIncremental && jarInput.status == Status.NOTCHANGED){
//                LogUtil.d(TAG,"jar name: $jarInput.name, NOTCHANGED")
//                break
//            }

            def md5Name = DigestUtils.md5Hex(jarInput.file.absolutePath)
            if (jarName.endsWith('.jar')) {
                jarName = jarName.substring(0, jarName.length() - 4)
            }
            //jar name: :module_test, jarInput.status: CHANGED
            def dest = outputProvider.getContentLocation(jarName + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR)

            println("jar jarInput：${jarInput.file.absolutePath} " +
                    "output jarName:$jarName, md5Name:$md5Name, dest: $dest.absolutePath")

            ScanHelper.scanJar(jarInput.file, dest, extension.classModifiers)

            FileUtils.copyFile(jarInput.file, dest)
        }
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        println "-------------------- ${getName()} transform(TransformInvocation transformInvocation) 开始-------------------"
        super.transform(transformInvocation)
        //写个文件把
        println "-------------------- ${getName()} transform(TransformInvocation transformInvocation) 结束-------------------"
    }
}
