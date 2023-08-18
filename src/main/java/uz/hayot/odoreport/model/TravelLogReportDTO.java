package uz.hayot.odoreport.model;

import lombok.Data;
import uz.hayot.odoreport.entity.TravelLogEntity;
import uz.hayot.odoreport.pojo.TravelLogPojo;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class TravelLogReportDTO {
    private List<TravelLogPojo> travelLogEntityList;
    private double totalDistance;
    private LocalDate date;

    public TravelLogReportDTO(List<TravelLogEntity> travelLogEntities, double totalDistance) {
        this.travelLogEntityList = travelLogEntities.stream().map(TravelLogEntity::toPojo).collect(Collectors.toList());
        this.totalDistance = totalDistance;
        /*When we get here we are sure that only a group with at least one element reach here*/
        date = travelLogEntities.get(0).getCreatedDate();
    }
}
