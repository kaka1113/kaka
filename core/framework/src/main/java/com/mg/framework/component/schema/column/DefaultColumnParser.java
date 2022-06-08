
package com.mg.framework.component.schema.column;

import org.apache.commons.codec.binary.Base64;

public class DefaultColumnParser extends ColumnParser {

    public  String colType;
    private String comment;

    public DefaultColumnParser(String colType, String comment) {
        this.colType = colType;
        this.comment = comment;
    }

    @Override
    public Object getValue(Object value) {

        if (value == null) {
            return null;
        }

        if (value instanceof byte[]) {
            return Base64.encodeBase64String((byte[]) value);
        }

        return value;
    }
}
