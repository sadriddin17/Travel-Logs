package uz.hayot.odoreport.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import uz.hayot.odoreport.pojo.TravelLogPojo;

import java.time.LocalDate;

@Table("travel_log")
@Getter
@Setter
@ToString
public class TravelLogEntity {
    @Id
    private Long id;

    @Column("created_date")
    private LocalDate createdDate;

    @Column("modified_date")
    private LocalDate modifiedDate;

    @Column("vehicle_registration_number")
    private String vehicleRegistrationNumber;

    @Column("vehicle_owner_name")
    private String VehicleOwnerName;

    @Column("odo_start")
    private Long odoStart;

    @Column("odo_end")
    private Long odoEnd;

    @Column("route")
    private String route;

    @Column("description")
    private String description;

    public TravelLogPojo toPojo() {
        return new TravelLogPojo(id, createdDate, odoStart, odoEnd, vehicleRegistrationNumber, getVehicleOwnerName(), route, description);
    }
}
