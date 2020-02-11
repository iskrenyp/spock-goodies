package com.github.iskrenyp.slackreporter

import com.github.iskrenyp.core.api.config.IConfigurableEntity
import com.ullink.slack.simpleslackapi.SlackPreparedMessage
import groovy.transform.TupleConstructor

@TupleConstructor
class SlackReporterConfigEntity implements IConfigurableEntity {

    String reportUrl
    String channelName
    String userToken
    String environment
    SlackPreparedMessage message
}
