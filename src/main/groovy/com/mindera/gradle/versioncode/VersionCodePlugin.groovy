package com.mindera.gradle.versioncode

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 *  Created by ricardovieira on 05/05/15.
 */
class VersionCodePlugin implements Plugin<Project> {

    private static final String TASK_PREFIX = "incrementVersionCode"

    private static final String GRADLE_GROUP = "Increment Version Code"

    void apply(Project project) {

        def log = project.logger

        def extension = project.extensions.create("appVersionCode", VersionCodePluginExtension)

        if (hasAndroidPlugin(project)) {

            project.android.applicationVariants.all { variant ->
                log.debug("Detected android plugin")
                if (variant.buildType.isDebuggable()) {
                    log.debug("Skipping debuggable build type ${variant.buildType.name}.")
                    return
                }

                def buildTypeName = variant.buildType.name.capitalize()

                def productFlavorNames = variant.productFlavors.collect { it.name.capitalize() }
                if (productFlavorNames.isEmpty()) {
                    productFlavorNames = [""]
                }
                def productFlavorName = productFlavorNames.join('')

                def variationName = "${productFlavorName}${buildTypeName}"

                def incrementVersionCodeTaskName = "${TASK_PREFIX}${variationName}"

                def incrementVersionCodeTask = project.tasks.
                        create(incrementVersionCodeTaskName, IncrementVersionCodeTask)
                incrementVersionCodeTask.appId = extension.appId.getOrElse(variant.applicationId)
                incrementVersionCodeTask.serviceEndpoint = extension.serviceEndpoint.get()
                incrementVersionCodeTask.enabled = extension.enabled.getOrElse(true)
                incrementVersionCodeTask.description =
                        "Increment version code for ${variationName} build type"
                incrementVersionCodeTask.group = GRADLE_GROUP
            }
        } else {
            log.debug("Android plugin not detected")
            def incrementVersionCodeTask = project.tasks.
                    create(TASK_PREFIX, IncrementVersionCodeTask)
            incrementVersionCodeTask.appId = extension.appId.get()
            incrementVersionCodeTask.serviceEndpoint = extension.serviceEndpoint.get()
            incrementVersionCodeTask.enabled = extension.enabled.getOrElse(true)
            incrementVersionCodeTask.description = "Increment version code"
            incrementVersionCodeTask.group = GRADLE_GROUP
        }
    }

    /**
     * Check if android plugin is applied
     * @param project Project
     * @return plugin applied
     */
    static boolean hasAndroidPlugin(Project project) {
        return project.plugins.hasPlugin("com.android.application")
    }
}

