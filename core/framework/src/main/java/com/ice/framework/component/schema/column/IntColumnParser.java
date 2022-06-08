
package com.ice.framework.component.schema.column;

public class IntColumnParser extends ColumnParser {

    private int bits;
    private boolean signed;
    private String colType;
    private String comment;

    public IntColumnParser(String dataType, String colType, String comment) {

        switch (dataType) {
            case "tinyint":
                bits = 8;
                break;
            case "smallint":
                bits = 16;
                break;
            case "mediumint":
                bits = 24;
                break;
            case "int":
                bits = 32;
        }

        this.signed = !colType.matches(".* unsigned$");
        this.colType = colType;
        this.comment = comment;
    }

    @Override
    public Object getValue(Object value) {

        if (value == null) {
            return null;
        }

        if (value instanceof Long) {
            return value;
        }

        if (value instanceof Integer) {
            Integer i = (Integer) value;
            if (signed || i > 0) {
                return i;
            } else {
                return (1L << bits) + i;
            }
        }

        return value;
    }
}
