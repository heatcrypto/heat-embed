package com.heatledger.embed;

import com.heatledger.Heat.ExternalAPIAdditions;

public class EmbedExternalAPIAdditions implements ExternalAPIAdditions{
    
    private Object[] externalResources;
    private Class<?>[] externalAnnotations;    
    
    public EmbedExternalAPIAdditions(Object[] externalResources, Class<?>[] externalAnnotations) {
        this.externalResources = externalResources;
        this.externalAnnotations = externalAnnotations;
    }

    @Override
    public Object[] getExternalResources() {
        return externalResources;
    }

    @Override
    public Class<?>[] getExternalAnnotations() {
        return externalAnnotations;
    }

}
