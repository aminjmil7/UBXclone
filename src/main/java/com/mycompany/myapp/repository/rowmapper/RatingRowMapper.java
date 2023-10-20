package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Rating;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Rating}, with proper type conversions.
 */
@Service
public class RatingRowMapper implements BiFunction<Row, String, Rating> {

    private final ColumnConverter converter;

    public RatingRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Rating} stored in the database.
     */
    @Override
    public Rating apply(Row row, String prefix) {
        Rating entity = new Rating();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setComment(converter.fromRow(row, prefix + "_comment", String.class));
        entity.setRatingNumber(converter.fromRow(row, prefix + "_rating_number", Integer.class));
        entity.setRatingDate(converter.fromRow(row, prefix + "_rating_date", Instant.class));
        entity.setUser_id(converter.fromRow(row, prefix + "_user_id", Integer.class));
        entity.setParkId(converter.fromRow(row, prefix + "_park_id", Long.class));
        entity.setEquipementId(converter.fromRow(row, prefix + "_equipement_id", Long.class));
        return entity;
    }
}
