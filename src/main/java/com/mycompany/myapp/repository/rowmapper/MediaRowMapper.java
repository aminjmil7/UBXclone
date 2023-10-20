package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Media;
import com.mycompany.myapp.domain.enumeration.AuthType;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Media}, with proper type conversions.
 */
@Service
public class MediaRowMapper implements BiFunction<Row, String, Media> {

    private final ColumnConverter converter;

    public MediaRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Media} stored in the database.
     */
    @Override
    public Media apply(Row row, String prefix) {
        Media entity = new Media();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFileName(converter.fromRow(row, prefix + "_file_name", String.class));
        entity.setFilePath(converter.fromRow(row, prefix + "_file_path", String.class));
        entity.setFileType(converter.fromRow(row, prefix + "_file_type", String.class));
        entity.setAuthType(converter.fromRow(row, prefix + "_auth_type", AuthType.class));
        entity.setParkId(converter.fromRow(row, prefix + "_park_id", Long.class));
        entity.setEquipementId(converter.fromRow(row, prefix + "_equipement_id", Long.class));
        entity.setReportId(converter.fromRow(row, prefix + "_report_id", Long.class));
        return entity;
    }
}
