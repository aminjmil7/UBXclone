package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Park;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Park}, with proper type conversions.
 */
@Service
public class ParkRowMapper implements BiFunction<Row, String, Park> {

    private final ColumnConverter converter;

    public ParkRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Park} stored in the database.
     */
    @Override
    public Park apply(Row row, String prefix) {
        Park entity = new Park();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setParkName(converter.fromRow(row, prefix + "_park_name", String.class));
        entity.setParkAddress(converter.fromRow(row, prefix + "_park_address", String.class));
        entity.setLongtitude(converter.fromRow(row, prefix + "_longtitude", BigDecimal.class));
        entity.setLatitude(converter.fromRow(row, prefix + "_latitude", BigDecimal.class));
        entity.setVerified(converter.fromRow(row, prefix + "_verified", Boolean.class));
        entity.setDateInstall(converter.fromRow(row, prefix + "_date_install", Instant.class));
        entity.setDateOpen(converter.fromRow(row, prefix + "_date_open", Instant.class));
        entity.setDateClose(converter.fromRow(row, prefix + "_date_close", Instant.class));
        entity.setNote(converter.fromRow(row, prefix + "_note", String.class));
        entity.setReseller(converter.fromRow(row, prefix + "_reseller", String.class));
        return entity;
    }
}
