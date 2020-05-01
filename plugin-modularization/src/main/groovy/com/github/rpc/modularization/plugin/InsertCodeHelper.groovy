package com.github.rpc.modularization.plugin

import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class InsertCodeHelper{

    static final String TAG = "InsertCodeHelper"

    static void insertInitCodeTo(ClassModifier classModifier) {
        File file = classModifier.codeInsertToClassFile
        if(file != null) {
            if (file.getName().endsWith('.jar'))
                generateCodeIntoJarFile(file, classModifier)
            else
                generateCodeIntoClassFile(file, classModifier)
        }
    }

    /**
     * 处理class的注入
     * @param file class文件
     * @return 修改后的字节码文件内容
     */
    private static byte[] generateCodeIntoClassFile(File file,ClassModifier classModifier) {
        def optClass = new File(file.getParent(), file.name + ".opt")

        FileInputStream inputStream = new FileInputStream(file)
        FileOutputStream outputStream = new FileOutputStream(optClass)

        def bytes = doGenerateCode(inputStream, classModifier)
        outputStream.write(bytes)
        inputStream.close()
        outputStream.close()
        if (file.exists()) {
            file.delete()
        }
        optClass.renameTo(file)
        return bytes
    }

    //处理jar包中的class代码注入
    private static File generateCodeIntoJarFile(File jarFile,ClassModifier classModifier) {
        if (jarFile) {
            def optJar = new File(jarFile.getParent(), jarFile.name + ".opt")
            if (optJar.exists())
                optJar.delete()
            def file = new JarFile(jarFile)
            Enumeration enumeration = file.entries()
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(optJar))

            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                String entryName = jarEntry.getName()
                ZipEntry zipEntry = new ZipEntry(entryName)
                InputStream inputStream = file.getInputStream(jarEntry)
                jarOutputStream.putNextEntry(zipEntry)
                LogUtil.d(TAG,"generateCodeIntoJarFile entryName:$entryName, codeInsertToClass:${classModifier.classModifierConfig.codeInsertToClass}")
                if (entryName ==
                        (classModifier.classModifierConfig.codeInsertToClass+".class")) {
                    println('generate code into:' + entryName)
                    def bytes = doGenerateCode(inputStream, classModifier)
                    jarOutputStream.write(bytes)
                } else {
                    jarOutputStream.write(IOUtils.toByteArray(inputStream))
                }
                inputStream.close()
                jarOutputStream.closeEntry()
            }
            jarOutputStream.close()
            file.close()

            if (jarFile.exists()) {
                jarFile.delete()
            }
            optJar.renameTo(jarFile)
        }
        return jarFile
    }

    private static byte[] doGenerateCode(InputStream inputStream,ClassModifier classModifier) {
        ClassReader cr = new ClassReader(inputStream)
        ClassWriter cw = new ClassWriter(cr, 0)
        ClassVisitor cv = new MyClassVisitor(Opcodes.ASM5, cw, classModifier)
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        return cw.toByteArray()
    }

    static class MyClassVisitor extends ClassVisitor {
        ClassModifier classModifier
        MyClassVisitor(int api, ClassVisitor cv,ClassModifier classModifier) {
            super(api, cv)
            this.classModifier = classModifier
        }

        void visit(int version, int access, String name, String signature,
                   String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces)
        }
        @Override
        MethodVisitor visitMethod(int access, String name, String desc,
                                  String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions)
            LogUtil.d(TAG,"MyClassVisitor name:$name, desc:$desc" +
                    ", codeInsertToMethod:${classModifier.classModifierConfig.codeInsertToMethod}, " +
                    "codeInsertToMethodParams:${classModifier.classModifierConfig.codeInsertToMethodParams}")

            if (name == classModifier.classModifierConfig.codeInsertToMethod &&
                desc == classModifier.classModifierConfig.codeInsertToMethodParams) { //注入代码到指定的方法之中

                LogUtil.i(TAG," visitMethod found name:$name, desc:$desc")

                boolean _static = (access & Opcodes.ACC_STATIC) > 0
                mv = new MyMethodVisitor(Opcodes.ASM5, mv, _static, classModifier)
            }
            return mv
        }
    }

    static class MyMethodVisitor extends MethodVisitor {
        boolean _static;
        ClassModifier classModifier

        MyMethodVisitor(int api, MethodVisitor mv, boolean _static, ClassModifier classModifier) {
            super(api, mv)
            this._static = _static
            this.classModifier = classModifier
        }

        @Override
        void visitInsn(int opcode) {
            classModifier.getICodeGenerator().modifyClass(classModifier,opcode, mv)
            super.visitInsn(opcode)
        }
        @Override
        void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(maxStack + 4, maxLocals)
        }
    }

}