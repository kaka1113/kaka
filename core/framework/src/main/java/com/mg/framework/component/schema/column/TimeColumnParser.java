
package com.mg.framework.component.schema.column;

import java.sql.Time;
import java.sql.Timestamp;

public class TimeColumnParser extends ColumnParser {

    private String colType;
    private String comment;
    public TimeColumnParser(String colType, String comment) {
        this.colType = colType;
        this.comment = comment;
    }

    @Override
    public Object getValue(Object value) {

        if (value == null) {
            return null;
        }

        if (value instanceof Timestamp) {

            return new Time(((Timestamp) value).getTime());
        }

        return value;
    }
}
