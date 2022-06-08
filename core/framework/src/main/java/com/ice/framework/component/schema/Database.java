
package com.ice.framework.component.schema;

import com.ice.framework.component.schema.column.ColumnParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Database {
    private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);

    private static final String SQL = "select table_name,column_name,data_type,column_type,character_set_name,column_comment " +
            "from information_schema.columns " +
            "where table_schema = ?";
    private String name;

    private DataSource dataSource;

    private Map<String, Table> tableMap = new HashMap<String, Table>();

    public Database(String name, DataSource dataSource) {
        this.name = name;
        this.dataSource = dataSource;
    }

    public void init() throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = dataSource.getConnection();

            ps = conn.prepareStatement(SQL);
            ps.setString(1, name);
            rs = ps.executeQuery();

            while (rs.next()) {
                String tableName = rs.getString(1);
                String colName = rs.getString(2);
                String dataType = rs.getString(3);
                String colType = rs.getString(4);
                String charset = rs.getString(5);
                String comment = rs.getString(6);

                ColumnParser columnParser = ColumnParser.getColumnParser(dataType, colType, charset,comment);

                if (!tableMap.containsKey(tableName)) {
                    addTable(tableName);
                }
                Table table = tableMap.get(tableName);
                table.addCol(colName);
                table.addParser(columnParser);
            }

        } finally {
            if (conn != null) {
                conn.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
        }

    }

    private void addTable(String tableName) {

        LOGGER.info("Schema load -- DATABASE:{},\tTABLE:{}", name, tableName);

        Table table = new Table(name, tableName);
        tableMap.put(tableName, table);
    }

    public Table getTable(String tableName) {

        return tableMap.get(tableName);
    }

    public Map<String, Table> getTableMap() {
        return tableMap;
    }
}
