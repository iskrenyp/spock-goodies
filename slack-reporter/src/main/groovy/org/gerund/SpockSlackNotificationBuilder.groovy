package org.gerund

import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy

@Builder(builderStrategy = ExternalStrategy, forClass = SpockSlackNotification)
class SpockSlackNotificationBuilder {
}
