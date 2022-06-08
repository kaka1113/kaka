
package com.ice.framework.component.schema.column;

import org.apache.commons.codec.Charsets;

public class StringColumnParser extends ColumnParser {

    private String charset;
    private String colType;
    private String comment;

    public StringColumnParser(String colType, String charset, String comment) {
        this.colType = colType;
        this.charset = charset.toLowerCase();
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

        byte[] bytes = (byte[]) value;

        switch (charset) {
            case "utf8":
            case "utf8mb4":
                return new String(bytes, Charsets.UTF_8);
            case "latin1":
            case "ascii":
                return new String(bytes, Charsets.ISO_8859_1);
            case "ucs2":
                return new String(bytes, Charsets.UTF_16);
            default:
                return new String(bytes, Charsets.toCharset(charset));

        }
    }
}
