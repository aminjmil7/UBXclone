package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ParkSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("park_name", table, columnPrefix + "_park_name"));
        columns.add(Column.aliased("park_address", table, columnPrefix + "_park_address"));
        columns.add(Column.aliased("longtitude", table, columnPrefix + "_longtitude"));
        columns.add(Column.aliased("latitude", table, columnPrefix + "_latitude"));
        columns.add(Column.aliased("verified", table, columnPrefix + "_verified"));
        columns.add(Column.aliased("date_install", table, columnPrefix + "_date_install"));
        columns.add(Column.aliased("date_open", table, columnPrefix + "_date_open"));
        columns.add(Column.aliased("date_close", table, columnPrefix + "_date_close"));
        columns.add(Column.aliased("note", table, columnPrefix + "_note"));
        columns.add(Column.aliased("reseller", table, columnPrefix + "_reseller"));

        return columns;
    }
}
