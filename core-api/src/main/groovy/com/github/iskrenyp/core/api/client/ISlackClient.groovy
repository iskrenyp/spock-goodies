package com.github.iskrenyp.core.api.client

import com.github.iskrenyp.core.api.exception.ISpockReporterException

trait ISlackClient {

    abstract ISlackClient connectToSlack(String userToken) throws ISpockReporterException

    abstract ISlackClient disconnectFromSlack() throws ISpockReporterException

    abstract ISlackClient sendMessage(String message, String channelName) throws ISpockReporterException

}