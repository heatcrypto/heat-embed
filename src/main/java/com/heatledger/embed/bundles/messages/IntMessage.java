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

public class IntMessage extends LongIdMessage {
    int value;

    @Override
    public void read(ByteBuffer buffer) {
        super.read(buffer);
        value = buffer.getInt();
    }

    @Override
    public void write(ByteBuffer buffer) {
        super.write(buffer);
        buffer.putInt(value);
    }

    @Override
    public void read(JSONObject json) {
        super.read(json);
        value = ((Number) json.get("value")).intValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void write(JSONObject json) {
        super.write(json);
        json.put("value", value);
    }
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof IntMessage && ((IntMessage)obj).value == value;
    }
}
