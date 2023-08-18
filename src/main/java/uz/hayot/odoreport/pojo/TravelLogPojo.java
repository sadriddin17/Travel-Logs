package uz.hayot.odoreport.pojo;

import java.time.LocalDate;

public record TravelLogPojo(long id, LocalDate date, long odometerStart, long odometerEnd, String vehicleRegistrationNumber, String VehicleOwnerName, String route, String description){}
