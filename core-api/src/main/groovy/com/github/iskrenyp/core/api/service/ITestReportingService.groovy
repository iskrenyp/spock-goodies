package com.github.iskrenyp.core.api.service

trait ITestReportingService {

    abstract def sendNotification() throws IOException

}