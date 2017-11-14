/*
 * Copyright (C) Heat Ledger Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Written by:
 *   - DM de Klerk <dennis@heatledger.com>, July 2017
 * */
package com.heatledger.embed.bundles;

import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;

import org.json.simple.JSONObject;

import com.heatledger.Transaction;

public abstract class AbstractBundleHandler<T extends BundleMessage> implements BundleHandler<T> {
    
    private Constructor<T> bundleMessageConstructor;
    private BundleFilter filter;

    public AbstractBundleHandler(Class<T> bundleMessageClass, BundleFilter filter) {
        try {
            this.bundleMessageConstructor = bundleMessageClass.getConstructor();
            this.filter = filter;
        }
        catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public abstract void apply(Transaction transaction, T message);
    
    @Override
    public boolean filter(Transaction transaction, boolean isConfirmed) {
        if (filter != null && !filter.filter(transaction, isConfirmed))
            return false;
        return true;
    }
    
    @Override
    public void validate(Transaction transaction, T message) throws BundleValidationException {}
    
    @Override
    public T getMessage(ByteBuffer buffer) {
        try {
            T message = this.bundleMessageConstructor.newInstance();
            message.read(buffer);
            return message;            
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }   
    
    @Override
    public T getMessage(JSONObject json) {
        try {
            T message = this.bundleMessageConstructor.newInstance();
            message.read(json);
            return message;            
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    } 
}
