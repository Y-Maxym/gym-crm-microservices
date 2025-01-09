package com.integration.tests;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.integration.tests")
@SelectClasspathResource("features")
public class CucumberRunnerTest {
}
