package com.github.rpc.modularization.plugin

import com.github.rpc.modularization.plugin.class_modifier.ClassModifier
import com.github.rpc.modularization.plugin.util.LogUtil
import org.objectweb.asm.Attribute
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

public class TestASMClassModify{

    // file 我理解的是 desc的class地址

    private final static String TAG = "TestASMClassModify"

    public static File recordModifyClassByDirectory(File dir, String root, File directoryDest){
        if (!dir.isDirectory()) {
           return null
        }

        def targetDestFile = null
        dir.eachFileRecurse { File file ->
            String filePath = file.absolutePath
            LogUtil.d(TAG,"filePath: $filePath ， file.isFile:${file.isFile()} destFile:$directoryDest")
            if(file.isFile()){
                def entryName = file.absolutePath.replace(root, '')
                def destFile = new File(directoryDest.absolutePath + File.separator + entryName)
                LogUtil.d(TAG,"recordModifyClassByDirectory entryName:$entryName destFile:$destFile")

                if(entryName == "com/github/rpc/modularization/test/Test.class"){
                    targetDestFile = destFile
                }
            }
        }

        return targetDestFile

    }

    public static testModifyClass(File file,String logString){
        LogUtil.d(TAG,"testModifyClass "+file)
        def optClass = new File(file.getParent(), file.name + ".opt")

        FileInputStream inputStream = new FileInputStream(file)
        FileOutputStream outputStream = new FileOutputStream(optClass)

        def bytes = doGenerateCode(inputStream,logString)
        outputStream.write(bytes)
        inputStream.close()
        outputStream.close()
        if (file.exists()) {
            file.delete()
        }
        optClass.renameTo(file)

    }

    private static byte[] doGenerateCode(InputStream inputStream,String logString) {
        ClassReader cr = new ClassReader(inputStream)
        ClassWriter cw = new ClassWriter(cr, 0)
        ClassVisitor cv = new MyClassVisitor(Opcodes.ASM5, cw, logString)
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        return cw.toByteArray()
    }

    static class MyClassVisitor extends ClassVisitor {

        private String logString;

        MyClassVisitor(int api, ClassVisitor cv, String logString) {
            super(api, cv)
            this.logString = logString
        }

        @Override
        void visitAttribute(Attribute attr) {
            super.visitAttribute(attr)
        }

        void visit(int version, int access, String name, String signature,
                   String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces)
        }
        @Override
        MethodVisitor visitMethod(int access, String name, String desc,
                                  String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions)
            LogUtil.d(TAG,"visitMethod name:$name, desc:$desc")
            mv = new MyMethodVisitor(Opcodes.ASM5, mv, logString)
            return mv
        }


    }

    static class MyMethodVisitor extends MethodVisitor {

        private String logString;

        MyMethodVisitor(int api, MethodVisitor mv, String logString) {
            super(api, mv)
            this.logString = logString;
        }

        @Override
        void visitCode() {
            super.visitCode()
        }

        @Override
        void visitEnd() {
            super.visitEnd()
        }

        @Override
        void visitInsn(int opcode) {

            modifyClass(opcode, mv, logString)

            super.visitInsn(opcode)
        }
        @Override
        void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(maxStack + 4, maxLocals)
        }

    }

    static void modifyClass(int opcode, MethodVisitor mv, String logString){
        if (opcode < Opcodes.IRETURN && opcode > Opcodes.RETURN) {
            return
        }

        LogUtil.d(TAG,"modifyClass start $logString ,opcode:$opcode")

        def label0 = new Label()// label代表什么意思？作用域吗？
        mv.visitLabel(label0)
        //是依靠行数来实现的吗？
        mv.visitLdcInsn("test")
        mv.visitLdcInsn(logString)
        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                     "android/util/Log",
                      "d",
                      "(Ljava/lang/String;Ljava/lang/String;)I",
                    false)
        mv.visitLdcInsn(Opcodes.POP)

        LogUtil.d(TAG,"modifyClass end $logString")

//        mv.visitMethodInsn(Opcodes.INVOKESTATIC
//                , classModifier.classModifierConfig.codeInsertToClass
//                , classModifier.classModifierConfig.callMethodName
//                , classModifier.classModifierConfig.callMethodParams
//                , false)

//        L0
//        LINENUMBER 8 L0
//        LDC "test"
//        LDC "TestASMClassModify"
//        INVOKESTATIC android/util/Log.d (Ljava/lang/String;Ljava/lang/String;)I
//        POP
//        L1
//        LINENUMBER 9 L1
//        RETURN
//        L2
//        LOCALVARIABLE this Lcom/github/rpc/modularization/test/Test; L0 L2 0
//        MAXSTACK = 2
//        MAXLOCALS = 1

    }

    //inputclass
    //classvisitor
    //methodvisitor
    //mv.modify

}