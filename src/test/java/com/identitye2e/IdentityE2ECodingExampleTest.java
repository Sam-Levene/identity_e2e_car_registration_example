package com.identitye2e;

import io.cucumber.junit.*;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features={"src/test/resources/features"},glue={"classpath:com.identitye2e.step_definitions"},plugin={"json:target/cucumber-results.json"})
public class IdentityE2ECodingExampleTest {
}