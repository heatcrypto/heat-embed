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

import com.heatledger.embed.bundles.AbstractBundleMessage;

public class LongIdMessage extends AbstractBundleMessage {
    public long id;

    @Override
    public void read(ByteBuffer buffer) {
        id = buffer.getLong();
    }

    @Override
    public void write(ByteBuffer buffer) {
        buffer.putLong(id);
    }

    @Override
    public void read(JSONObject json) {
        id = Long.parseUnsignedLong((String) json.get("id"));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void write(JSONObject json) {
        json.put("id", Long.toUnsignedString(id));
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof LongIdMessage && ((LongIdMessage)obj).id == id;
    }
}
