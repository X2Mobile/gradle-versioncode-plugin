package com.mindera.gradle.versioncode

import com.android.build.api.variant.AndroidComponentsExtension
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

            def androidComponents = project.extensions.getByType(AndroidComponentsExtension)

            androidComponents.onVariants { variant ->

                log.debug("Detected android plugin")
                if (variant.buildType.get().isDebuggable()) {
                    log.debug("Skipping debuggable build type ${variant.buildType.get().name}.")
                    return
                }

                def buildTypeName = variant.buildType.get().name.capitalize()

                def flavorNames = variant.flavors.collect { it.name.capitalize() }
                if (flavorNames.isEmpty()) {
                    flavorNames = [""]
                }
                def flavorName = flavorNames.join('')

                def variationName = "${flavorName}${buildTypeName}"

                def incrementVersionCodeTaskName = "${TASK_PREFIX}${variationName}"

                def incrementVersionCodeTask = project.tasks.
                        create(incrementVersionCodeTaskName, IncrementVersionCodeTask)
                incrementVersionCodeTask.appId = extension.appId.getOrElse(variant.applicationId.get())
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

