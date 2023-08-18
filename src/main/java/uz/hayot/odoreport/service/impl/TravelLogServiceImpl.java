package uz.hayot.odoreport.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uz.hayot.odoreport.entity.TravelLogEntity;
import uz.hayot.odoreport.model.TravelLogDTO;
import uz.hayot.odoreport.model.TravelLogReportDTO;
import uz.hayot.odoreport.pojo.TravelLogPojo;
import uz.hayot.odoreport.repository.TravelLogRepository;
import uz.hayot.odoreport.service.TravelLogService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TravelLogServiceImpl implements TravelLogService {
    private final TravelLogRepository travelLogRepository;

    public TravelLogServiceImpl(TravelLogRepository travelLogRepository) {
        this.travelLogRepository = travelLogRepository;
    }

    @Override
    public Mono<TravelLogPojo> createTravelLog(TravelLogDTO travelLogDTO) {
        Assert.hasText(travelLogDTO.vehicleRegistrationNumber(), "vehicle registration number cannot be blank!");
        Assert.hasText(travelLogDTO.vehicleOwnerName(), "vehicle owner name cannot be blank!");
        Assert.isTrue(travelLogDTO.odoStart() <= travelLogDTO.odoEnd(), "invalid odometer values!");
        Assert.hasText(travelLogDTO.route(), "route cannot be blank!");

        //TODO
        return travelLogRepository.save(travelLogDTO.toEntity());// need to create pojo class
    }

    @Override
    public Mono<Void> updateTravelLog(Long id, TravelLogDTO updatedLog) {
        return travelLogRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Travel log not found!")))
                .flatMap(oldEntity -> {
                    oldEntity.setModifiedDate(LocalDate.now());
                    if (!updatedLog.vehicleRegistrationNumber().isBlank())
                        oldEntity.setVehicleRegistrationNumber(updatedLog.vehicleRegistrationNumber());
                    if (!updatedLog.vehicleOwnerName().isBlank())
                        oldEntity.setVehicleOwnerName(updatedLog.vehicleOwnerName());
                    if (!updatedLog.route().isBlank()) oldEntity.setRoute(updatedLog.route());
                    if (!updatedLog.description().isBlank()) oldEntity.setDescription(updatedLog.description());
                    return travelLogRepository.update(oldEntity);
                })
                .then();
    }

    @Override
    //TODO
    //it is better that return type is boolean
    public Mono<Void> deleteTravelLog(Long id) {
        Assert.notNull(id, "ID cannot be null!");
        return travelLogRepository
                .deleteById(id);
    }


    @Override
    public Flux<List<TravelLogReportDTO>> generateReport(LocalDate startDate, LocalDate endDate,
                                                         String vehicleRegistrationNumber, String vehicleOwnerName) {
        Flux<TravelLogEntity> filteredLogs = travelLogRepository.findByFilters(startDate, endDate,
                vehicleRegistrationNumber, vehicleOwnerName);

        return filteredLogs.collectList()
                .flatMapMany(travelLogs -> {
                    // Group travel logs by createdDate
                    Map<LocalDate, List<TravelLogEntity>> groupedByDate = travelLogs.stream()
                            .collect(Collectors.groupingBy(TravelLogEntity::getCreatedDate));

                    // Create a Flux emitting each list of logs with the same createdDate
                    return Flux.fromIterable(groupedByDate.values());
                })
                .map(logs -> {
                    double totalDistance = calculateTotalDistance(logs); // Assuming you have a method to calculate total distance
                    return new TravelLogReportDTO(logs, totalDistance);
                })
                .collectList()
                .flux(); // Convert the final list back to a Flux
    }



    private double calculateTotalDistance(List<TravelLogEntity> travelLogEntities) {
        long totalDistance = 0L;
        for (TravelLogEntity log : travelLogEntities) {
            totalDistance += log.getOdoEnd() - log.getOdoStart();
        }
        return totalDistance;
    }
}

