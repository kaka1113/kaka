

package com.mg.framework.component.schema;



import com.mg.framework.component.schema.column.ColumnParser;

import java.util.LinkedList;
import java.util.List;

public class Table {
    private String database;
    private String name;
    private List<String> colList = new LinkedList<>();
    private List<ColumnParser> parserList = new LinkedList<>();

    public Table(String database, String table) {
        this.database = database;
        this.name = table;
    }

    public void addCol(String column) {
        colList.add(column);
    }

    public void addParser(ColumnParser columnParser) {
        parserList.add(columnParser);
    }

    public List<String> getColList() {
        return colList;
    }

    public String getDatabase() {
        return database;
    }

    public String getName() {
        return name;
    }

    public List<ColumnParser> getParserList() {
        return parserList;
    }


}