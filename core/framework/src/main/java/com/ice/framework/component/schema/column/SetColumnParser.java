
package com.ice.framework.component.schema.column;

public class SetColumnParser extends ColumnParser {

    private String[] enumValues;
    private String comment;

    public SetColumnParser(String colType, String comment) {
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

        StringBuilder builder = new StringBuilder();
        long l = (Long) value;

        boolean needSplit = false;
        for (int i = 0; i < enumValues.length; i++) {
            if (((l >> i) & 1) == 1) {
                if (needSplit)
                    builder.append(",");

                builder.append(enumValues[i]);
                needSplit = true;
            }
        }

        return builder.toString();
    }
}
