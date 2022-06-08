
package com.mg.framework.component.schema;

import com.mg.framework.util.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Schema {
    private static final Logger LOGGER = LoggerFactory.getLogger(Schema.class);

    private static final String SQL = "select schema_name from information_schema.schemata";

    private static final List<String> IGNORED_DATABASES = new ArrayList<>(
            Arrays.asList(new String[]{"information_schema", "mysql", "performance_schema", "sys"})
    );

    private DataSource dataSource;

    private Map<String, Database> dbMap;

    public Schema(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void load(String dbName) throws SQLException {

        dbMap = new HashMap<>();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = dataSource.getConnection();
            String suffix = null;
            if (ObjectUtils.isNotEmpty(dbName)) {
                suffix = String.format(" where schema_name = '%s'", dbName);
            }
            ps = conn.prepareStatement(ObjectUtils.isNotEmpty(suffix) ? SQL.concat(suffix) : SQL);
            rs = ps.executeQuery();

            while (rs.next()) {
                dbName = rs.getString(1);
                if (!IGNORED_DATABASES.contains(dbName)) {
                    Database database = new Database(dbName, dataSource);
                    dbMap.put(dbName, database);
                }
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

        for (Database db : dbMap.values()) {
            db.init();
        }

    }

    public Database getDatabase(String dbName) {

        if (dbMap == null) {
            reload(dbName);
        }

        Database database = dbMap.get(dbName);
        if (database == null) {
            return null;
        }

        return database;
    }

    public Table getTable(String dbName, String tableName) {

        if (dbMap == null) {
            reload(dbName);
        }

        Database database = dbMap.get(dbName);
        if (database == null) {
            return null;
        }

        Table table = database.getTable(tableName);
        if (table == null) {
            return null;
        }

        return table;
    }

    private void reload(String dbName) {

        while (true) {
            try {
                load(dbName);
                break;
            } catch (Exception e) {
                LOGGER.error("Reload schema error.", e);
            }
        }
    }

    public void reset() {
        dbMap = null;
    }
}
