/*
 * Copyright (C) Heat Ledger Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Written by:
 *   - DM de Klerk <dennis@heatledger.com>, July 2017
 * */
package com.heatledger.embed.bundles;

import org.json.simple.JSONObject;

public abstract class AbstractBundleMessage implements BundleMessage {

    @Override
    public String toString() {
        return super.toString() + " [JSON ["+toJSON().toJSONString()+"]]";
    }
    
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        write(json); 
        return json;
    }
    
    protected boolean stringEquals(String thisString, String otherString) {
        if (thisString == null)
            return otherString == null;
        return thisString.equals(otherString);
    }
}
