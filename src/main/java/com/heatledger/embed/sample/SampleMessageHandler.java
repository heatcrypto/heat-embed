package com.heatledger.embed.sample;

import java.sql.SQLException;

import com.heatledger.Transaction;
import com.heatledger.embed.bundles.AbstractBundleHandler;
import com.heatledger.embed.bundles.BundleFilter;

public class SampleMessageHandler extends AbstractBundleHandler<SampleMessageMessage> {

    private SampleMessageDB db;

    public SampleMessageHandler(SampleMessageDB db, BundleFilter filter) {
        super(SampleMessageMessage.class, filter);
        this.db = db;
    }

    @Override
    public void apply(Transaction transaction, SampleMessageMessage message) {
        try {
            db.create(transaction, message);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
