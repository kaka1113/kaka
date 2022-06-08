
package com.ice.framework.component.schema.column;

import java.sql.Date;
import java.util.Calendar;

public class YearColumnParser extends ColumnParser {

    private String colType;
    private String comment;
    public YearColumnParser(String colType, String comment) {
        this.colType = colType;
        this.comment = comment;
    }

    @Override
    public Object getValue(Object value) {

        if (value == null) {
            return null;
        }

        if (value instanceof Date) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime((Date) value);
            return calendar.get(Calendar.YEAR);
        }

        return value;
    }
}
