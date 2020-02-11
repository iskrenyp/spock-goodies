package com.github.iskrenyp.slackreporter.service

import com.github.iskrenyp.slackreporter.SlackReporterConfigEntity

trait ISlackReportingService {

    abstract SlackReporterConfigEntity prepareNotification(String reporterName, Integer failsCount, List<String> failedMethods)

    abstract def sendNotification(SlackReporterConfigEntity configEntity)

}