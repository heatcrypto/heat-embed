/*
 * Copyright (C) Heat Ledger Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Written by:
 *   - DM de Klerk <dennis@heatledger.com>, July 2017
 * */
package com.heatledger.embed.replicator;

import com.heatledger.Heat;
import com.heatledger.Transaction;
import com.heatledger.TransactionProcessor;
import com.heatledger.embed.bundles.BundleProcessor;
import com.heatledger.replicate.AbstractReplicator;
import com.heatledger.util.Listener;

import java.util.List;

public class MasterReplicator extends AbstractReplicator {

    private AbstractReplicator[] models;
    private BundleProcessor bundleProcessor;

    public MasterReplicator(BundleProcessor bundleProcessor, AbstractReplicator[] subModels) {
        super("master_replicator_db");
        this.bundleProcessor = bundleProcessor; 
        models = new AbstractReplicator[subModels.length + 1];
        for (int i=0; i<subModels.length; i++)
            models[i] = subModels[i];
        models[models.length - 1] = this;
    }

    private Listener<List<? extends Transaction>> addedConfirmedListener = transactions -> {
        bundleProcessor.processTransactions(transactions, true);
    };

    private Listener<List<? extends Transaction>> addedUnconfirmedListener = transactions -> {
        bundleProcessor.processTransactions(transactions, false);
    };

    public AbstractReplicator[] getModels() {
        return models;
    }

    @Override
    public void registerListeners() {
        Heat.getTransactionProcessor().addListener(addedConfirmedListener,
                TransactionProcessor.Event.ADDED_CONFIRMED_TRANSACTIONS);
        Heat.getTransactionProcessor().addListener(addedUnconfirmedListener,
                TransactionProcessor.Event.ADDED_UNCONFIRMED_TRANSACTIONS);
    }

    @Override
    public void beforeFullRescan() {
        for (Object model : models) {
            if (model instanceof BasicReplicator<?>) {
                ((BasicReplicator<?>) model).clear();
            }
        }
    }

    @Override
    public void afterFullRescan() {}

}
