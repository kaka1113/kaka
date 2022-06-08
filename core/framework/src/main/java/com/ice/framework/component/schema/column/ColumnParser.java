
package com.ice.framework.component.schema.column;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ColumnParser {



    public static ColumnParser getColumnParser(String dataType, String colType, String charset,String comment) {
        switch (dataType) {
            case "tinyint":
            case "smallint":
            case "mediumint":
            case "int":
                return new IntColumnParser(dataType, colType,comment);
            case "bigint":
                return new BigIntColumnParser(colType,comment);
            case "tinytext":
            case "text":
            case "mediumtext":
            case "longtext":
            case "varchar":
            case "char":
                return new StringColumnParser(colType,charset,comment);
            case "date":
            case "datetime":
            case "timestamp":
                return new DateTimeColumnParser(colType,comment);
            case "time":
                return new TimeColumnParser(colType,comment);
            case "year":
                return new YearColumnParser(colType,comment);
            case "enum":
                return new EnumColumnParser(colType,comment);
            case "set":
                return new SetColumnParser(colType,comment);
            default:
                return new DefaultColumnParser(colType,comment);
        }
    }

    public static String[] extractEnumValues(String colType) {
        String[] enumValues = {};
        Matcher matcher = Pattern.compile("(enum|set)\\((.*)\\)").matcher(colType);
        if (matcher.matches()) {
            enumValues = matcher.group(2).replace("'", "").split(",");
        }

        return enumValues;
    }

    public abstract Object getValue(Object value);

}
