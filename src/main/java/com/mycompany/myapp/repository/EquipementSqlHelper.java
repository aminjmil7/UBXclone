package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class EquipementSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("model_name", table, columnPrefix + "_model_name"));
        columns.add(Column.aliased("model_number", table, columnPrefix + "_model_number"));
        columns.add(Column.aliased("instruction", table, columnPrefix + "_instruction"));
        columns.add(Column.aliased("verified", table, columnPrefix + "_verified"));

        columns.add(Column.aliased("park_id", table, columnPrefix + "_park_id"));
        return columns;
    }
}
