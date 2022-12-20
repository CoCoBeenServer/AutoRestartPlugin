package com.cocobeen.Bungee.Utils;

import java.util.Base64;

public class Base64Decode {

    public static String DecodeString(String message){
        byte[] decodedBytes = Base64.getDecoder().decode(message.getBytes());
        String decodedStr = new String(decodedBytes);
        return decodedStr;
    }
}