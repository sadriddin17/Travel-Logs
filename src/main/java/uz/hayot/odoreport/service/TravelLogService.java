package uz.hayot.odoreport.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uz.hayot.odoreport.model.TravelLogDTO;
import uz.hayot.odoreport.model.TravelLogReportDTO;
import uz.hayot.odoreport.pojo.TravelLogPojo;

import java.time.LocalDate;
import java.util.List;

public interface TravelLogService {
    Mono<TravelLogPojo> createTravelLog(TravelLogDTO travelLogDTO);
    Mono<Void> updateTravelLog(Long id, TravelLogDTO travelLogDTOr);
    Mono<Void> deleteTravelLog(Long id);
    Flux<List<TravelLogReportDTO>> generateReport(LocalDate startDate, LocalDate endDate,
                                                  String vehicleRegistrationNumber, String vehicleOwnerName);
}
