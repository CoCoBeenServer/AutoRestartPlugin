package com.cocobeen.Bukkit.Utils;

import java.util.Base64;

public class Base64Encode {

    public static String EncodeString(String message){
        byte[] encodedBytes = Base64.getEncoder().encode(message.getBytes());
        String encodedStr = new String(encodedBytes);
        return encodedStr;
    }
}