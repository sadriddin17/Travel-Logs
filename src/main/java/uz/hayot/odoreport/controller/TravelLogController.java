package uz.hayot.odoreport.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uz.hayot.odoreport.model.TravelLogDTO;
import uz.hayot.odoreport.model.TravelLogReportDTO;
import uz.hayot.odoreport.pojo.TravelLogPojo;
import uz.hayot.odoreport.service.TravelLogService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/travel-logs")
//@Api(tags = "Travel Log APIs")
public class TravelLogController {
    private final TravelLogService travelLogService;

    public TravelLogController(TravelLogService travelLogService) {
        this.travelLogService = travelLogService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new travel log")
    public Mono<TravelLogPojo> createTravelLog(@RequestBody TravelLogDTO travelLogDTO) {
        return travelLogService.createTravelLog(travelLogDTO);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Edit an existing travel log")
    public Mono<Void> editTravelLog(@PathVariable Long id, @RequestBody TravelLogDTO travelLogDTO) {
        return travelLogService.updateTravelLog(id, travelLogDTO);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a travel log")
    public Mono<Void> deleteTravelLog(@PathVariable Long id) {
        return travelLogService.deleteTravelLog(id);
    }

    @GetMapping("report")
    @Operation(summary = "Get a report about all travels with or without filters")
    public Flux<List<TravelLogReportDTO>> generateReport(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String vehicleRegistrationNumber,
            @RequestParam(required = false) String vehicleOwnerName) {
        return travelLogService.generateReport(startDate, endDate,
                vehicleRegistrationNumber, vehicleOwnerName);
    }
}
