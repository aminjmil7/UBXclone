package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class MediaSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("file_name", table, columnPrefix + "_file_name"));
        columns.add(Column.aliased("file_path", table, columnPrefix + "_file_path"));
        columns.add(Column.aliased("file_type", table, columnPrefix + "_file_type"));
        columns.add(Column.aliased("auth_type", table, columnPrefix + "_auth_type"));

        columns.add(Column.aliased("park_id", table, columnPrefix + "_park_id"));
        columns.add(Column.aliased("equipement_id", table, columnPrefix + "_equipement_id"));
        columns.add(Column.aliased("report_id", table, columnPrefix + "_report_id"));
        return columns;
    }
}
