package com.github.rpc.modularization.plugin.scan


import com.github.rpc.modularization.plugin.util.LogUtil
import com.github.rpc.modularization.plugin.class_modifier.ClassModifier
import com.github.rpc.modularization.plugin.config.ClassModifierConfig
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.regex.Pattern

class ScanHelper {

    static String TAG = "ScanHelper"

    /**
     *  扫描目录 找到
     *  //收集到待注入的模块 和 模块服务实现
     *  //检测到被注入模块的class
     * @param path
     * @param project
     */
    static void scanDirectory(String path,
                              String root,
                              List<ClassModifier> classModifierList,
                              File destFile){
        LogUtil.d(TAG,"test")
        File dir = new File(path)
        if (dir.isDirectory()) {
            dir.eachFileRecurse { File file ->
                String filePath = file.absolutePath
                LogUtil.d(TAG,"filePath: $filePath ， file.isFile:${file.isFile()}")
                if(file.isFile()){
                    scanClassFile(file, root, classModifierList, destFile)
                }
            }
        }
    }

    static boolean scanJar(File jarFile, File destFile,List<ClassModifier> classModifierList) {
        //检查是否存在缓存，有就添加class list 和 设置fileContainsInitClass
        if (!jarFile )
            return false

        def file = new JarFile(jarFile)
        Enumeration enumeration = file.entries()

        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement()
            String entryName = jarEntry.getName()

            LogUtil.d(TAG,"scanJar entryName:$entryName")

            //support包不扫描
            if (entryName.startsWith("android/support"))
                break
            //是否要过滤这个类，这个可配置
            def shouldProcessClass = shouldProcessClass(entryName, classModifierList)
            LogUtil.d(TAG,"scanJar shouldProcessClass:$shouldProcessClass")
            if (shouldProcessClass) {
                InputStream inputStream = file.getInputStream(jarEntry)
                scanClass(inputStream, jarFile.absolutePath, classModifierList, destFile)
                inputStream.close()
            }
        }
        if (null != file) {
            file.close()
        }
        return true
    }

    static boolean shouldProcessClass(String entryName,List<ClassModifier> classModifierList) {
        if (entryName == null || !entryName.endsWith(".class"))
            return false

        //过滤
        for(int i=0; i<classModifierList.size() ;i++){
            def it = classModifierList.get(i)
            def shouldProcessThisClass = shouldProcessThisClass(it,entryName)
            LogUtil.d(TAG,"shouldProcessClass entryName:${entryName} shouldProcessThisClass:${shouldProcessThisClass}")
            if(shouldProcessThisClass){
                return true
            }
        }

        return false
    }

    /**
     * 过滤器进行过滤
     * @param info
     * @param entryName
     * @return
     */
    private static boolean shouldProcessThisClass(ClassModifier classModifier, String entryName) {
        ClassModifierConfig classModifierConfig = classModifier.classModifierConfig
        def list = classModifierConfig.includePatterns
        if (list) {
            def exlist = classModifierConfig.excludePatterns
            Pattern pattern, p
            for (int i = 0; i < list.size(); i++) {
                pattern = list.get(i)
                if (pattern.matcher(entryName).matches()) {
                    if (exlist) {
                        for (int j = 0; j < exlist.size(); j++) {
                            p = exlist.get(j)
                            if (p.matcher(entryName).matches())
                                return false
                        }
                    }
                    return true
                }
            }
        }
        return false
    }


    static void scanClassFile(File classFile,
                              String root,
                              List<ClassModifier> classModifierList,
                              File destFile){

        if(classModifierList.size() == 0) return
//        def shouldProcessClass = shouldProcessClass(entryName, classModifierList)
//        LogUtil.d(TAG,"scanJar shouldProcessClass:$shouldProcessClass")
        def entryName = classFile.absolutePath.replace(root, '')
        def shouldProcessClass = shouldProcessClass(entryName,classModifierList)
        LogUtil.d(TAG,"scanClassFile entryName:$entryName shouldProcessClass:$shouldProcessClass classFile:$classFile")

        if(shouldProcessClass){
            scanClass(classFile.newInputStream(),
                      classFile.absolutePath,
                      classModifierList,
                      destFile)
        }
    }



    static boolean scanClass(InputStream inputStream,
                             String filePath,
                             List<ClassModifier> classModifierList,
                             File destFile) {

        int api = getAsmApiLevel()
        try {
            ClassReader cr = new ClassReader(inputStream)
            ClassWriter cw = new ClassWriter(cr, 0)
            ScanClassVisitor cv = new ScanClassVisitor(api, cw, filePath, classModifierList, destFile)
            cr.accept(cv, ClassReader.EXPAND_FRAMES)
            inputStream.close()

            return cv.found
        } catch (Throwable throwable) {
            System.err.println("\n>>>>>>>>>>>>ERROR: An error occurred while scanning class file(built by jdk: ):" +
                    "\n\t" + filePath)
            if (throwable instanceof IllegalArgumentException) {
                System.err.println("Maybe the current ASM(${api >> 16}.x) version is not compatible with the jdk version that compiled this class. " +
                        "\nTo resolve this problem, please update your gradle version to use higher ASM version, " +
                        "or rebuild your class/jar with lower level JDK." +
                        "\n\nFor example: We first encountered this problem with Gson 2.8.6 which built by jdk version 53 (JDK9), " +
                        "\nwe need ASM6 to scan its classes(update android gradle plugin to 3.2.0 or higher)\n")
            }
            throw throwable
        }
    }

    private static int ASM_LEVEL = 0
    static int getAsmApiLevel() {
        if (ASM_LEVEL > 0) return ASM_LEVEL
        int api = Opcodes.ASM5
        for (i in (10..5)) {
            try {
                def field = Opcodes.class.getDeclaredField("ASM" + i)
                if (field != null) {
                    api = field.get(null)
                    break
                }
            } catch (Throwable ignored) {
            }
        }
        ASM_LEVEL = api
        return ASM_LEVEL
    }

    static class MyClassReader extends ClassReader {

        MyClassReader(InputStream is) throws IOException {
            super(is)
        }

        @Override
        short readShort(int index) {
            def s = super.readShort(index)
//            if (index == 6) {
//                // cache the last class file compiled JDK version before crash happens
//                lastClassBuildVersion = s
//            }
            return s
        }
    }

    static short lastClassBuildVersion;

    static class ScanClassVisitor extends ClassVisitor {
        private String filePath
        private String destFile
        private def found = false
        private ClassModifier[] classModifierList;
        private String annotationDesc;
        private ClassInfo classInfo = new ClassInfo()

        ScanClassVisitor(int api,
                         ClassVisitor cv,
                         String filePath,
                         List<ClassModifier> classModifierList,
                         File destFile) {
            super(api, cv)
            this.filePath = filePath
            this.classModifierList = classModifierList
            this.destFile = destFile
        }

        boolean is(int access, int flag) {
            return (access & flag) == flag
        }

        boolean isFound() {
            return found
        }

        void visit(int version, int access, String name, String signature,
                   String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces)
            //抽象类、接口、非public等类无法调用其无参构造方法
            if (is(access, Opcodes.ACC_ABSTRACT)
                    || is(access, Opcodes.ACC_INTERFACE)
                    || !is(access, Opcodes.ACC_PUBLIC)
            ) {
                return
            }
            LogUtil.d(TAG,"visit name:$name, annotationDesc:$annotationDesc," +
                    "signature:$signature, superName:$superName," +
                    " sourcefilePath:$filePath, destFile:$destFile")

            classInfo.destFilePath = destFile
            classInfo.version = version
            classInfo.access = access
            classInfo.name = name
            classInfo.signature = signature
            classInfo.superName = superName
            classInfo.interfaces = interfaces
            LogUtil.d(TAG,"visit classInfo:$classInfo, classInfo.name:${classInfo.name}")
            classModifierList.each {
                it.recordClassModifierTarget(classInfo)
            }
        }

        @Override
        AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            LogUtil.d(TAG,"visitAnnotation classInfo.name:${classInfo.name} classInfo${classInfo} desc:$desc sourcefilePath:$filePath, destFile:$destFile")
            classInfo.annotationDesc = desc
            classModifierList.each {
                it.recordClassModifierTarget(classInfo)
            }

            return super.visitAnnotation(desc, visible)
        }
    }

}