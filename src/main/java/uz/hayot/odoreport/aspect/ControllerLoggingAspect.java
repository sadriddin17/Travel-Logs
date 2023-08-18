package uz.hayot.odoreport.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uz.hayot.odoreport.model.TravelLogDTO;
import uz.hayot.odoreport.model.TravelLogReportDTO;
import uz.hayot.odoreport.pojo.TravelLogPojo;

import java.net.InetAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Aspect
@Component
public class ControllerLoggingAspect {
    private final Logger logger = LoggerFactory.getLogger(ControllerLoggingAspect.class);


    @Around(value = "execution(* uz.hayot.odoreport.controller.TravelLogController.createTravelLog(..)) && args(travelLogDTO, ..)", argNames = "pjp,travelLogDTO")
    public Object aroundCreateTravelLogExecution(ProceedingJoinPoint pjp, TravelLogDTO travelLogDTO) {
        String guid = UUID.randomUUID().toString();
        final LocalDateTime requestTime = LocalDateTime.now();
        loggingRequest(travelLogDTO.toString(), pjp.getArgs(), guid, requestTime);
        try {
            Mono<TravelLogPojo> resultMono = (Mono<TravelLogPojo>) pjp.proceed();

            return resultMono.doOnNext(pojo -> {
                loggingResponse(HttpStatus.CREATED, pojo.toString(), guid, requestTime);
            });
        } catch (Throwable e) {
            loggingResponse(HttpStatus.NOT_IMPLEMENTED, e.getMessage(), guid, requestTime);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Around(value = "execution(* uz.hayot.odoreport.controller.TravelLogController.generateReport(..)) && args(..)", argNames = "pjp")
    public Object aroundGenerateReportExecution(ProceedingJoinPoint pjp) {
        String guid = UUID.randomUUID().toString();
        final LocalDateTime requestTime = LocalDateTime.now();
        loggingRequest("", pjp.getArgs(), guid, requestTime);
        try {
            Flux<List<TravelLogReportDTO>> resultMono = (Flux<List<TravelLogReportDTO>>) pjp.proceed();

            return resultMono.doOnNext(pojo -> {
                String response = "Reported total list size: " + pojo.stream().mapToInt(it -> it.getTravelLogEntityList().size()).sum() + " totalDistance: " + pojo.stream().mapToDouble(TravelLogReportDTO::getTotalDistance).sum();
                loggingResponse(HttpStatus.OK, response, guid, requestTime);
            });
        } catch (Throwable e) {
            loggingResponse(HttpStatus.NOT_IMPLEMENTED, e.getMessage(), guid, requestTime);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Around(value = "execution(* uz.hayot.odoreport.controller.TravelLogController.editTravelLog(..)) && args(travelLogDTO, ..)", argNames = "pjp,travelLogDTO")
    public Void aroundEditTravelLog(ProceedingJoinPoint pjp, TravelLogDTO travelLogDTO) {
        String guid = UUID.randomUUID().toString();
        final LocalDateTime requestTime = LocalDateTime.now();
        loggingRequest(travelLogDTO.toString(), pjp.getArgs(), guid, requestTime);
        try {
            pjp.proceed();
            loggingResponse(HttpStatus.ACCEPTED, "", guid, requestTime);
        } catch (Throwable e) {
            loggingResponse(HttpStatus.NOT_IMPLEMENTED, e.getMessage(), guid, requestTime);
            throw new RuntimeException(e.getMessage());
        }
        return null;
    }

    @Around(value = "execution(* uz.hayot.odoreport.controller.TravelLogController.deleteTravelLog(..)) && args(..)", argNames = "pjp")
    public Void aroundDeleteTravelLog(ProceedingJoinPoint pjp) {
        String guid = UUID.randomUUID().toString();
        final LocalDateTime requestTime = LocalDateTime.now();
        loggingRequest("", pjp.getArgs(), guid, requestTime);
        try {
            pjp.proceed();
            loggingResponse(HttpStatus.NO_CONTENT, "", guid, requestTime);
        } catch (Throwable e) {
            loggingResponse(HttpStatus.NOT_IMPLEMENTED, e.getMessage(), guid, requestTime);
            throw new RuntimeException(e.getMessage());
        }
        return null;
    }


    @SneakyThrows
    private void loggingRequest(final String body, final Object[] parameters, final String guid, final LocalDateTime requestTime) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        final JSONObject content = new JSONObject();
        content.put("requestId", guid);
        content.put("type", "request");
        content.put("method", request.getMethod());
        content.put("uri", request.getRequestURI());
        content.put("body", body);
        content.put("Parameters", Arrays.toString(parameters));
        content.put("service", "OdoReport");
        content.put("remote", InetAddress.getLoopbackAddress().getHostAddress());
        content.put("timestamp", requestTime);
        logger.info(content.toString());
    }

    @SneakyThrows
    private void loggingResponse(final HttpStatusCode statusCode, String body, final String guid, final LocalDateTime requestTime) {
        final LocalDateTime now = LocalDateTime.now();
        final JSONObject content = new JSONObject();
        content.put("requestId", guid);
        content.put("timestamp", now);
        content.put("status", statusCode);
        content.put("duration", Duration.between(requestTime, now).toMillis());
        content.put("type", "response");
        content.put("body", body);
        logger.info(content.toString());
    }
}

