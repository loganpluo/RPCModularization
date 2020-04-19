package com.github.rpc.modularization.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project


class ModularizationPlugin implements Plugin<Project> {

    void apply(Project project) {
        println()
        println("=========== ${project.name} ModularizationPlugin start=============")

        ModuleDebugAbleHelper.doDebugAbleModule(project)

    }
}