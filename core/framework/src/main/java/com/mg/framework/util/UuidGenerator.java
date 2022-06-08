package com.mg.framework.util;

import java.util.UUID;

/**
 * @author hubo
 * @since 2020/2/25
 */
public class UuidGenerator {

    public static String[] chars = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    public UuidGenerator() {
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String shortUuid() {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        int eight = 8;
        for (int i = 0; i < eight; ++i) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 62]);
        }

        return shortBuffer.toString();
    }

    public static String randomNumber(int length) {
        String strTable = "1234567890";
        int len = strTable.length();
        boolean bDone = true;

        String retStr;
        do {
            retStr = "";
            int count = 0;

            for (int i = 0; i < length; ++i) {
                double dblR = Math.random() * (double) len;
                int intR = (int) Math.floor(dblR);
                char c = strTable.charAt(intR);
                if (48 <= c && c <= 57) {
                    ++count;
                }

                retStr = retStr + strTable.charAt(intR);
            }

            int two = 2;
            if (count >= two) {
                bDone = false;
            }
        } while (bDone);

        return retStr;
    }
}
