package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Events;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Events}, with proper type conversions.
 */
@Service
public class EventsRowMapper implements BiFunction<Row, String, Events> {

    private final ColumnConverter converter;

    public EventsRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Events} stored in the database.
     */
    @Override
    public Events apply(Row row, String prefix) {
        Events entity = new Events();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setEventName(converter.fromRow(row, prefix + "_event_name", String.class));
        entity.setEventDate(converter.fromRow(row, prefix + "_event_date", Instant.class));
        entity.setUser_id(converter.fromRow(row, prefix + "_user_id", Integer.class));
        entity.setParkId(converter.fromRow(row, prefix + "_park_id", Long.class));
        entity.setEquipementId(converter.fromRow(row, prefix + "_equipement_id", Long.class));
        return entity;
    }
}
