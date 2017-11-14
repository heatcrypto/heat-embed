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

import com.heatledger.Transaction;

public interface BundleHandler<T extends BundleMessage> extends BundleFilter {
    
    /**
     * Will create and return an instance of <T extends BundleMessage>
     * 
     * @param ByteBuffer or JSONObject
     * @return
     */
    public T getMessage(ByteBuffer buffer);
    public T getMessage(JSONObject json);
    
    /**
     * Determines if a message is valid and allowed to be applied
     *
     * @throws BundleValidationException
     */
    public void validate(Transaction transaction, T message) throws BundleValidationException;
    
    /**
     * Applies the bundle message
     * 
     * @param transaction
     * @param message
     */
    public void apply(Transaction transaction, T message);
}
