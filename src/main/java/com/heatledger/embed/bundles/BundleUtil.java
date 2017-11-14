/*
 * Copyright (C) Heat Ledger Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Written by:
 *   - DM de Klerk <dennis@heatledger.com>, July 2017
 * */
package com.heatledger.embed.bundles;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.BooleanSupplier;

import com.heatledger.Constants;
import com.heatledger.Heat;
import com.heatledger.util.Convert;

public class BundleUtil {
    
    static final String EMPTY_STRING = "";

    static void invalid(String message) throws BundleValidationException {
        throw new BundleValidationException(message);
    }
    
    public static byte[] toArray(short bundleId, BundleMessage message) {
        ByteBuffer buffer = ByteBuffer.allocate(Constants.MAX_ARBITRARY_MESSAGE_LENGTH).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(bundleId);
        message.write(buffer);
        byte[] array = new byte[buffer.position()];
        buffer.rewind();
        buffer.get(array);
        return array;
    }
    
    static void validate(String message, BooleanSupplier validator) throws BundleValidationException {
        if (!validator.getAsBoolean()) {
            invalid(message);
        }
    }
    
    static void validateLength(String value, String fieldName, int minLength, int maxLength) throws BundleValidationException {
        if (value != null) {
            if (value.length() < minLength) {
                invalid(fieldName + " too short, min length " + minLength);
            }
            if (value.length() > maxLength) {
                invalid(fieldName + " too long, max length " + minLength);
            }                
        }
    }
    
    static void validateNonEmpty(String value, String fieldName) throws BundleValidationException {
        if (value == null) {
            invalid(fieldName + " cannot be null");
        }
        if (value.trim().length() == 0) {
            invalid(fieldName + " cannot be empty");
        }
    }
    
    static void validateTimestamp(int value, int allowedLowerBoudaryOffset, int allowedUpperBoudaryOffset) throws BundleValidationException {
        int timestamp = Heat.getBlockchain().getLastBlock().getTimestamp();
        if (timestamp > value && value - timestamp > allowedLowerBoudaryOffset) {
            invalid("timestamp to far from the future");
        }
        else if (timestamp < value && timestamp - value > allowedUpperBoudaryOffset) {
            invalid("timestamp to old");
        }
    }
    
    public static String readString(ByteBuffer buffer) {
        short length = buffer.getShort();
        if (length == 0) {
            return EMPTY_STRING;
        }
        byte[] bytes = new byte[length];
        for (int i=0; i<length; i++) {
            bytes[i] = buffer.get(); 
        }
        return Convert.toString(bytes);
    }
    
    public static void writeString(ByteBuffer buffer, String value) {
        byte[] bytes = Convert.toBytes(value);
        short length = (short) bytes.length;
        buffer.putShort(length);
        for (int i=0; i<length; i++) {
            buffer.put(bytes[i]);
        }
    }
}
