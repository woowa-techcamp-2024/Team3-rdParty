package com.thirdparty.ticketing;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.logging.LogLevel;

public class MockLambdaLogger implements LambdaLogger {
    @Override
    public void log(String message) {
        System.out.println(message);
    }

    @Override
    public void log(byte[] message) {
        System.out.println(message);
    }

    @Override
    public void log(String message, LogLevel logLevel) {
        LambdaLogger.super.log(message, logLevel);
    }

    @Override
    public void log(byte[] message, LogLevel logLevel) {
        LambdaLogger.super.log(message, logLevel);
    }
}
