package com.github.rpc.modularization.plugin

import com.android.tools.r8.org.objectweb.asm.MethodVisitor

interface ICodeGenerator{
    public void modifyClass(ClassModifier extension, int opcode, org.objectweb.asm.MethodVisitor mv)
}