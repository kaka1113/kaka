
package com.ice.framework.component.schema.column;

public class EnumColumnParser extends ColumnParser {

    private String[] enumValues;
    private String comment;

    public EnumColumnParser(String colType, String comment) {
        enumValues = extractEnumValues(colType);
        this.comment = comment;
    }

    @Override
    public Object getValue(Object value) {

        if (value == null) {
            return null;
        }

        if (value instanceof String) {
            return value;
        }

        Integer i = (Integer) value;
        if (i == 0) {
            return null;
        } else {
            return enumValues[i - 1];
        }
    }
}
