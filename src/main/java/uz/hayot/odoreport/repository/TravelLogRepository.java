package uz.hayot.odoreport.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import uz.hayot.odoreport.entity.TravelLogEntity;
import uz.hayot.odoreport.pojo.TravelLogPojo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class TravelLogRepository {

    private final JdbcTemplate jdbcTemplate;

    public TravelLogRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Mono<TravelLogEntity> update(TravelLogEntity travelLogEntity) {
        String sql = "UPDATE travel_log SET created_date = ?, vehicle_registration_number = ?, vehicle_owner_name = ?, " +
                "odo_start = ?, odo_end = ?, route = ?, description = ? WHERE id = ?";

        jdbcTemplate.update(sql, preparedStatement -> {
            preparedStatement.setObject(1, travelLogEntity.getCreatedDate());
            preparedStatement.setString(2, travelLogEntity.getVehicleRegistrationNumber());
            preparedStatement.setString(3, travelLogEntity.getVehicleOwnerName());
            preparedStatement.setLong(4, travelLogEntity.getOdoStart());
            preparedStatement.setLong(5, travelLogEntity.getOdoEnd());
            preparedStatement.setString(6, travelLogEntity.getRoute());
            preparedStatement.setString(7, travelLogEntity.getDescription());
            preparedStatement.setLong(8, travelLogEntity.getId());
        });

        return Mono.just(travelLogEntity);
    }

    public Mono<TravelLogPojo> save(TravelLogEntity travelLogEntity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sql = "INSERT INTO travel_log (created_date, vehicle_registration_number, vehicle_owner_name, odo_start, odo_end, route, description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, travelLogEntity.getCreatedDate());
            ps.setString(2, travelLogEntity.getVehicleRegistrationNumber());
            ps.setString(3, travelLogEntity.getVehicleOwnerName());
            ps.setLong(4, travelLogEntity.getOdoStart());
            ps.setLong(5, travelLogEntity.getOdoEnd());
            ps.setString(6, travelLogEntity.getRoute());
            ps.setString(7, travelLogEntity.getDescription());
            return ps;
        }, keyHolder);

        travelLogEntity.setId((Long) keyHolder.getKeyList().get(0).get("id"));

        return Mono.just(travelLogEntity.toPojo());
    }




    public Mono<TravelLogEntity> findById(Long id) {
        String sql = "SELECT * FROM travel_log WHERE id = ?";

        return Mono.fromCallable(() -> jdbcTemplate.queryForObject(sql, new Object[]{id},
                        (rs, rowNum) -> mapRowToTravelLogEntity(rs)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteById(Long id) {
        String sql = "DELETE FROM travel_log WHERE id = ?";

        return Mono.fromRunnable(() -> jdbcTemplate.update(sql, id));
    }


    public Flux<TravelLogEntity> findByFilters(LocalDate startDate, LocalDate endDate,
                                               String vehicleRegistrationNumber, String vehicleOwnerName) {
        String sql = "SELECT * FROM travel_log WHERE " +
                "(created_date >= COALESCE(?, created_date)) " +
                "AND (created_date <= COALESCE(?, created_date)) " +
                "AND (vehicle_registration_number = COALESCE(?, vehicle_registration_number)) " +
                "AND (vehicle_owner_name = COALESCE(?, vehicle_owner_name)) " +
                "ORDER BY odo_start";

        List<TravelLogEntity> travelLogs = jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToTravelLogEntity(rs),
                startDate, endDate, vehicleRegistrationNumber, vehicleOwnerName);

        return Flux.fromIterable(travelLogs);
    }


    private TravelLogEntity mapRowToTravelLogEntity(ResultSet rs) throws SQLException {
        TravelLogEntity travelLogEntity = new TravelLogEntity();
        travelLogEntity.setId(rs.getLong("id"));
        travelLogEntity.setCreatedDate(rs.getObject("created_date", LocalDate.class));
        travelLogEntity.setModifiedDate(rs.getObject("modified_date", LocalDate.class));
        travelLogEntity.setVehicleRegistrationNumber(rs.getString("vehicle_registration_number"));
        travelLogEntity.setVehicleOwnerName(rs.getString("vehicle_owner_name"));
        travelLogEntity.setOdoStart(rs.getLong("odo_start"));
        travelLogEntity.setOdoEnd(rs.getLong("odo_end"));
        travelLogEntity.setRoute(rs.getString("route"));
        travelLogEntity.setDescription(rs.getString("description"));
        return travelLogEntity;
    }


}
