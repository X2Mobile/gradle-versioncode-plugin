package com.mindera.gradle.versioncode

import org.gradle.api.provider.Property

/**
 * Created by renatoalmeida on 23/03/16.
 */
interface VersionCodePluginExtension {

    Property<String> getAppId()

    Property<String> getServiceEndpoint()

    Property<Boolean> getEnabled()
}
