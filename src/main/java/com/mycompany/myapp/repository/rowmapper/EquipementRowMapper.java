package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Equipement;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Equipement}, with proper type conversions.
 */
@Service
public class EquipementRowMapper implements BiFunction<Row, String, Equipement> {

    private final ColumnConverter converter;

    public EquipementRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Equipement} stored in the database.
     */
    @Override
    public Equipement apply(Row row, String prefix) {
        Equipement entity = new Equipement();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setModelName(converter.fromRow(row, prefix + "_model_name", String.class));
        entity.setModelNumber(converter.fromRow(row, prefix + "_model_number", String.class));
        entity.setInstruction(converter.fromRow(row, prefix + "_instruction", String.class));
        entity.setVerified(converter.fromRow(row, prefix + "_verified", Boolean.class));
        entity.setParkId(converter.fromRow(row, prefix + "_park_id", Long.class));
        return entity;
    }
}
