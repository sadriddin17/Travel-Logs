package uz.hayot.odoreport.model;

import uz.hayot.odoreport.entity.TravelLogEntity;

import java.time.LocalDate;

public record TravelLogDTO(String vehicleRegistrationNumber, String vehicleOwnerName, long odoStart, long odoEnd,
                           String route, String description) {
    public TravelLogEntity toEntity() {
        TravelLogEntity entity = new TravelLogEntity();
        entity.setCreatedDate(LocalDate.now());
        entity.setVehicleRegistrationNumber(this.vehicleRegistrationNumber);
        entity.setVehicleOwnerName(this.vehicleOwnerName);
        entity.setOdoStart(this.odoStart);
        entity.setOdoEnd(this.odoEnd);
        entity.setRoute(this.route);
        entity.setDescription(this.description);
        return entity;
    }
}
