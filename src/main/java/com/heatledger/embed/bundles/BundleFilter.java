/*
 * Copyright (C) Heat Ledger Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Written by:
 *   - DM de Klerk <dennis@heatledger.com>, July 2017
 * */
package com.heatledger.embed.bundles;

import com.heatledger.Transaction;

@FunctionalInterface
public interface BundleFilter {
    
    /**
     * Called before processing any transaction, must return true in order for
     * this transaction to be further processed by this handler.
     * 
     * @param transaction
     * @return
     */
    public boolean filter(Transaction transaction, boolean isConfirmed);
}
