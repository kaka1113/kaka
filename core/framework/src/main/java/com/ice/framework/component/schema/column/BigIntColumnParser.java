
package com.ice.framework.component.schema.column;

import java.math.BigInteger;

public class BigIntColumnParser extends ColumnParser {

    private static BigInteger max = BigInteger.ONE.shiftLeft(64);

    private boolean signed;
    private String colType;
    private String comment;

    public BigIntColumnParser(String colType, String comment) {
        this.signed = !colType.matches(".* unsigned$");
        this.colType = colType;
        this.comment = comment;
    }

    @Override
    public Object getValue(Object value) {

        if (value == null) {
            return null;
        }

        if (value instanceof BigInteger) {
            return value;
        }

        Long l = (Long) value;
        if (!signed && l < 0) {
            return max.add(BigInteger.valueOf(l));
        } else {
            return l;
        }
    }
}
