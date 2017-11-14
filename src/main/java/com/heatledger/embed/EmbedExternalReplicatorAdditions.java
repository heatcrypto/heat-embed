package com.heatledger.embed;

import javax.inject.Provider;

import com.heatledger.Heat.ExternalReplicatorAdditions;
import com.heatledger.embed.bundles.BundleProcessor;
import com.heatledger.embed.replicator.MasterReplicator;
import com.heatledger.replicate.AbstractReplicator;

public class EmbedExternalReplicatorAdditions implements ExternalReplicatorAdditions {
    
    private MasterReplicator masterReplicator = null;
    private Provider<BundleProcessor> bundleProcessor;
    private Provider<AbstractReplicator[]> models;
    
    public EmbedExternalReplicatorAdditions(Provider<BundleProcessor> bundleProcessor, Provider<AbstractReplicator[]> models) {
        this.bundleProcessor = bundleProcessor;
        this.models = models;
    }
    
    @Override
    public AbstractReplicator[] getExternalReplicatorAdditions() {
        if (masterReplicator == null) {
            masterReplicator = new MasterReplicator(bundleProcessor.get(), models.get());
        }
        return masterReplicator.getModels();
    }

}
