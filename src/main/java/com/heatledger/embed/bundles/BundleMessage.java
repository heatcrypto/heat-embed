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

import org.json.simple.JSONObject;

public interface BundleMessage {

    public void read(ByteBuffer buffer);
    
    public void write(ByteBuffer buffer);
    
    public void read(JSONObject json);
    
    public void write(JSONObject json);
}
