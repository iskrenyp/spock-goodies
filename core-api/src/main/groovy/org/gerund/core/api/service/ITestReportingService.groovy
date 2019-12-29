package org.gerund.core.api.service

trait ITestReportingService {

    abstract def sendNotification() throws IOException

}