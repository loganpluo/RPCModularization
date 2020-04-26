package com.github.rpc.modularization.plugin

import com.android.tools.r8.org.objectweb.asm.MethodVisitor

interface ICodeGenerator{
    public MethodVisitor createMethodVisitor(ClassModifyInfo extension)
}