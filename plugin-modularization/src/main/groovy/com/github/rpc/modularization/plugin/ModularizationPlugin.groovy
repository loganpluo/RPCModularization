package com.github.rpc.modularization.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

public class ModularizationPlugin implements Plugin<Project> {

    void apply(Project project) {
        System.out.println("========================");
        System.out.println("hello gradle ModularizationPlugin!");
        System.out.println("========================");
    }
}