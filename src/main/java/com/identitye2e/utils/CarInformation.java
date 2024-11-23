package com.identitye2e.utils;

import java.util.Objects;

public class CarInformation {
    private String manufacturer;
    private String registrationNumber;
    private String modelOfCar;
    private String yearCarMade;

    public CarInformation() {
        registrationNumber = "";
        modelOfCar = "";
        yearCarMade = "";
        manufacturer = "";
    }

    public CarInformation(String manu, String regi, String model, String year) {
        registrationNumber = regi;
        modelOfCar = model;
        yearCarMade = year;
        manufacturer = manu;
    }


    public String getYearCarMade() {
        return yearCarMade;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModelOfCar() {
        return modelOfCar;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setModelOfCar(String modelOfCar) {
        this.modelOfCar = modelOfCar;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public void setYearCarMade(String yearCarMade) {
        this.yearCarMade = yearCarMade;
    }

    public boolean isEqual(CarInformation otherCar) {
        return Objects.equals(this.manufacturer, otherCar.manufacturer)
                && Objects.equals(this.modelOfCar, otherCar.modelOfCar)
                && Objects.equals(this.registrationNumber, otherCar.registrationNumber)
                && Objects.equals(this.yearCarMade, otherCar.yearCarMade);
    }
}
