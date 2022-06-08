
package com.mg.framework.component.schema.column;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeColumnParser extends ColumnParser {

    private static SimpleDateFormat dateTimeFormat;
    private static SimpleDateFormat dateTimeUtcFormat;

    static {
        dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateTimeUtcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateTimeUtcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    String colType ;
    private String comment;
    public DateTimeColumnParser(String colType, String comment) {
        this.colType = colType;
        this.comment = comment;
    }

    @Override
    public Object getValue(Object value) {

        if (value == null) {
            return null;
        }

        if (value instanceof Timestamp) {
            return dateTimeFormat.format(value);
        }

        if (value instanceof Long) {
            return dateTimeUtcFormat.format(new Date((Long) value));
        }

        return value;
    }
}
