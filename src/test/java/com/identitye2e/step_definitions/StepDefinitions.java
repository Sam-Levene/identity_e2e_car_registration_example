package com.identitye2e.step_definitions;

import com.identitye2e.reporting.ReportOverview;
import com.identitye2e.runner.*;
import com.identitye2e.utils.*;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class StepDefinitions {
    private final Logger logger = LogManager.getLogger(StepDefinitions.class);
    private boolean isRun = false;
    private boolean isSetup = false;
    private int iterator;
    private List<String> carRegistrationPlates;
    private CarInformation carInformationActual = new CarInformation();
    private List<CarInformation> actualCarListings = new ArrayList<>();
    private SuiteMeta suiteMeta;
    private BrowserMeta browserMeta;
    private BrowserRunTime browserRunTime;
    private BrowserActions browserActions;

    @Before
    public void setupEnvironment(Scenario scenario) throws IOException, AWTException {
        CustomScreenRecorder customScreenRecorder = new CustomScreenRecorder(new File(BrowserRunTime.USER_DIR + "/report/"));
        List<Map<String, String>> propertyList = FileProperties.readPropertiesAsList(System.getProperties());

        for(Map<String, String> propertyMap : propertyList) {
            for(Map.Entry<String, String> propertyMapEntry : propertyMap.entrySet()) {
                if(propertyMapEntry.getKey().equals("cucumber.options")) {
                    for (String tagName : scenario.getSourceTagNames()) {
                        if (propertyMapEntry.getValue().contains(tagName)) {
                            isRun = true;
                            if (!isSetup) {
                                suiteMeta = new SuiteMeta.Builder().withGroups(Collections.singletonList(System.getProperties().toString())).build();
                                Runtime.getRuntime().addShutdownHook(new Thread(this::startClosure));
                                isSetup = true;

                            }

                            String featureName = "Feature-" + scenario.getId();
                            String scenarioName = "Scenario-" + iterator +"-" + scenario.getName().replace(" ", "-");
                            EnvironmentPropertyReader environmentPropertyReader = new EnvironmentPropertyReader("Identity_E2E");
                            browserMeta = new BrowserMeta.Builder()
                                    .withConfiguration("Identity_E2E")
                                    .withReference("Test Reference")
                                    .withDescription("IdentityE2E Tech Task")
                                    .withUrl(environmentPropertyReader.getProperties().getProperty("desktopUrl"))
                                    .withGroups("IdentityE2E")
                                    .withSimulator("Not Specified")
                                    .withProperties(environmentPropertyReader.getProperties())
                                    .withInvokingClass(featureName)
                                    .withMethodName(scenarioName)
                                    .withPropertiesList(null)
                                    .build();

                            String pattern = "dd-MM-yyyy";
                            String dateInString = new SimpleDateFormat(pattern).format(new Date());
                            customScreenRecorder.startRecording(browserMeta.getClassName() + "-"
                                    + environmentPropertyReader.getProperties().getProperty("browserType") + "-"
                                    + browserMeta.getMethodName() + "-"
                                    + browserMeta.getSimulator().replace(" ", "_") + "-"
                                    + dateInString +"-Recording");
                            browserRunTime = new BrowserRunTime.Builder().withBrowserMeta(browserMeta).build();
                            browserActions = new BrowserActions();
                            iterator++;
                        }
                    }
                }
            }
        }
    }

    @Given("^I have loaded the registration numbers of the cars I wish to check$")
    public void iHaveLoadedTheRegistrationNumbersOfTheCarsIWishToCheck() throws BrowserException, InterruptedException {
        if (isRun) {
            logger.info("Starting Selenium WebDriver");
            browserActions.setCucumberFlag(true);
            browserActions.setDriver(BrowserRunTime.getDriver());
            CarRegistrationScanner carRegistrationScanner = new CarRegistrationScanner();
            carRegistrationScanner.readCarRegistrationFromFile(System.getProperty("user.dir") + "\\files\\car_input.txt");
            // carRegistrationScanner.readCarRegistrationFromFile(System.getProperty("user.dir") + "/files/car_input.txt"); (Use this if on a non-windows machine)

            carRegistrationPlates = carRegistrationScanner.getActualCarRegistrationNumbers();
            logger.info("Navigating to the WeBuyAnyCar site");
            browserActions.focus(Locator.id("onetrust-accept-btn-handler")).touch();
            Thread.sleep(3000);
        }
    }

    @When("^I navigate to the WeBuyAnyCar Website and enter the loaded registration numbers$")
    public void iNavigateToTheWeBuyAnyCarWebsiteAndEnterTheLoadedRegistrationNumbers() throws BrowserException, InterruptedException {
        if (isRun) {
            for(String registrationPlate : carRegistrationPlates) {
                browserActions.focus(Locator.id("vehicleReg"))
                        .compose(registrationPlate);
                browserActions.focus(Locator.id("Mileage"))
                        .compose("32000");
                browserActions.focus(Locator.id("btn-go"))
                        .touch();
                Thread.sleep(3000);
                try {
                    browserActions.focus(Locator.xpath("/html/body/div[1]/wbac-app/div[1]/div/div/vehicle-questions/div/section[1]/div/div[1]/div/div[3]/div/vehicle-details/div[3]/div[2]"))
                            .collect(Locator.tag("div"));
                    carInformationActual.setRegistrationNumber(registrationPlate);
                    for (int iterator = 0; iterator < browserActions.getFocusedList().size(); iterator++) {
                        browserActions.select(iterator);
                        String text = browserActions.getText();
                        String[] textSplit = text.split(": ");
                        if (textSplit.length == 2) {
                            if (Objects.equals(textSplit[0], "manufacturer"))
                                carInformationActual.setManufacturer(textSplit[1]);
                            else if (Objects.equals(textSplit[0], "model")) {
                                carInformationActual.setModelOfCar(textSplit[1]);
                            } else if (Objects.equals(textSplit[0], "year")) {
                                carInformationActual.setYearCarMade(textSplit[1]);
                            }
                        }
                    }
                    actualCarListings.add(carInformationActual);
                } catch (Exception e) {
                    logger.error("Couldn't locate car registration plate; why not?");
                }
                browserActions.changeUrl("https://www.webuyanycar.com");
            }
        }
    }

    @Then("^I confirm that the value is correct for the mileage given$")
    public void iConfirmThatTheValueIsCorrectForTheMileageGiven() throws BrowserException {
        if (isRun) {
            List<CarInformation> expectedCarListings = new ArrayList<>();
            CarInformation expectedCars = new CarInformation();
            CarRegistrationScanner carRegistrationScanner = new CarRegistrationScanner();
            int matchedCars = 0;

            carRegistrationScanner.checkCarRegistrationAgainstExpectedOutput(System.getProperty("user.dir") + "\\files\\car_output.txt");
            // carRegistrationScanner.checkCarRegistrationAgainstExpectedOutput(System.getProperty("user.dir") + "/files/car_output.txt"); (Use this if on a non-windows machine)
            List<String> expectedOutput = carRegistrationScanner.getExpectedCarRegistrationNumbers();

            for (int iterator = 1; iterator < expectedOutput.size(); iterator++) {
                String[] expectedOutputSplit = expectedOutput.get(iterator).split(",");
                expectedCars.setRegistrationNumber(expectedOutputSplit[0]);
                expectedCars.setManufacturer(expectedOutputSplit[1]);
                expectedCars.setModelOfCar(expectedOutputSplit[2]);
                expectedCars.setYearCarMade(expectedOutputSplit[3]);
                expectedCarListings.add(expectedCars);
            }

            for (CarInformation expectedCar : expectedCarListings) {
                for (CarInformation actualCar : actualCarListings) {
                    if (expectedCar.isEqual(actualCar)) {
                        matchedCars++;
                        break;
                    }
                }
            }

            Assert.assertEquals(4, matchedCars);
        }
    }

    private void startClosure() {
        suiteMeta.close();
        ReportOverview reportOverview = new ReportOverview(suiteMeta);
        reportOverview.run();
        if (System.getProperty("openreport").equals("true")) {
            new BrowserRunTime("file:///" + System.getProperty("user.dir") + "/report/reportOverview.html");
        }
    }
}