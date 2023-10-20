package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Report;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Report}, with proper type conversions.
 */
@Service
public class ReportRowMapper implements BiFunction<Row, String, Report> {

    private final ColumnConverter converter;

    public ReportRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Report} stored in the database.
     */
    @Override
    public Report apply(Row row, String prefix) {
        Report entity = new Report();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setMail(converter.fromRow(row, prefix + "_mail", String.class));
        entity.setMessage(converter.fromRow(row, prefix + "_message", String.class));
        entity.setDate(converter.fromRow(row, prefix + "_date", Instant.class));
        entity.setEquipementId(converter.fromRow(row, prefix + "_equipement_id", Long.class));
        entity.setParkId(converter.fromRow(row, prefix + "_park_id", Long.class));
        return entity;
    }
}
