package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ReportSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("mail", table, columnPrefix + "_mail"));
        columns.add(Column.aliased("message", table, columnPrefix + "_message"));
        columns.add(Column.aliased("date", table, columnPrefix + "_date"));

        columns.add(Column.aliased("equipement_id", table, columnPrefix + "_equipement_id"));
        columns.add(Column.aliased("park_id", table, columnPrefix + "_park_id"));
        return columns;
    }
}
