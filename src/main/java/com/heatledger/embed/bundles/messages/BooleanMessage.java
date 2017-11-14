/*
 * Copyright (C) Heat Ledger Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Written by:
 *   - DM de Klerk <dennis@heatledger.com>, July 2017
 * */
package com.heatledger.embed.bundles.messages;

import java.nio.ByteBuffer;

import org.json.simple.JSONObject;

public class BooleanMessage extends LongIdMessage {
    public boolean value;

    @Override
    public void read(ByteBuffer buffer) {
        super.read(buffer);
        value = buffer.get() != 0;
    }

    @Override
    public void write(ByteBuffer buffer) {
        super.write(buffer);
        buffer.put(value ? (byte) 1 : 0);
    }

    @Override
    public void read(JSONObject json) {
        super.read(json);
        value = Boolean.TRUE.equals(json.get("value"));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void write(JSONObject json) {
        super.write(json);
        json.put("value", value);
    }
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof BooleanMessage && ((BooleanMessage)obj).value == value;
    }
}
