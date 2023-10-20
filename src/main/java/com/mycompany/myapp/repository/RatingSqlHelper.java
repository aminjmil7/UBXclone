package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class RatingSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("comment", table, columnPrefix + "_comment"));
        columns.add(Column.aliased("rating_number", table, columnPrefix + "_rating_number"));
        columns.add(Column.aliased("rating_date", table, columnPrefix + "_rating_date"));
        columns.add(Column.aliased("user_id", table, columnPrefix + "_user_id"));

        columns.add(Column.aliased("park_id", table, columnPrefix + "_park_id"));
        columns.add(Column.aliased("equipement_id", table, columnPrefix + "_equipement_id"));
        return columns;
    }
}
