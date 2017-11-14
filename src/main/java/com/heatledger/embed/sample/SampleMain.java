package com.heatledger.embed.sample;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.heatledger.Account;
import com.heatledger.Transaction;
import com.heatledger.crypto.Crypto;
import com.heatledger.embed.Embed;
import com.heatledger.embed.EmbedBuilder;
import com.heatledger.embed.EmbedExternalAPIAdditions;
import com.heatledger.embed.EmbedExternalReplicatorAdditions;
import com.heatledger.embed.bundles.BundleFilter;
import com.heatledger.embed.bundles.BundleHandler;
import com.heatledger.embed.bundles.BundleMessage;
import com.heatledger.embed.bundles.BundleProcessor;
import com.heatledger.replicate.AbstractReplicator;

public class SampleMain {
    
    public static short SAMPLE_MESSAGE_ID = 1;

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.setProperty("heat.microservicesEnabled", "false");
        properties.setProperty("heat.enableWebsockets","false");
        properties.setProperty("heat.enableAPIServer","false");
        //properties.setProperty("heat.isOffline","true");  
        properties.setProperty("heat.replicatorEnabled","true");
        
        new SampleMain(args, properties);
    }
    
    private Embed embed;
    private BundleProcessor bundleProcessor;
    private Map<Short, BundleHandler<? extends BundleMessage>> handlers = new HashMap<Short, BundleHandler<? extends BundleMessage>>();
    
    /* Database Models go here */
    private SampleMessageDB sampleMessageDB;
    
    public SampleMain(String[] args, Properties properties) {
        
        /* Create the embedding */
        embed = new EmbedBuilder(SampleMain.class)
                .properties(properties)
                .externalAPIAdditions(createSampleExternalAPIAdditions())
                .externalReplicatorAdditions(createSampleExternalReplicatorAdditions())
                .build();
        embed.start(args);
    }
    
    private EmbedExternalAPIAdditions createSampleExternalAPIAdditions() {
        
        /* Place all your API resources here */
        Object[] resources = new Object[] { new ResourceSample() };
        
        /* Place all your annotated classes here */        
        Class<?>[] annotations = new Class<?>[] { Models.class };
        
        return new EmbedExternalAPIAdditions(resources, annotations);
    }
    
    private EmbedExternalReplicatorAdditions createSampleExternalReplicatorAdditions() {
        String mySecretPhrase = "hello";
        byte[] myPublicKey = Crypto.getPublicKey(mySecretPhrase);
        long myAccount = Account.getId(myPublicKey);
        
        return new EmbedExternalReplicatorAdditions(
                () -> {                    
                    /* Create the bundle processor */
                    bundleProcessor = createBundleProcessor(mySecretPhrase);
                    return bundleProcessor;
                }, 
                () -> {
                    
                    /* Create the models */
                    sampleMessageDB = new SampleMessageDB();
                    
                    /* Create and add all handlers */
                    handlers.put(SAMPLE_MESSAGE_ID, new SampleMessageHandler(sampleMessageDB, createBundleFilter(myAccount)));                    
                
                    return new AbstractReplicator[] {  sampleMessageDB };
                });
    }
    
    private BundleProcessor createBundleProcessor(String secretPhrase) {        
        return new BundleProcessor(secretPhrase) {

            @Override
            public BundleHandler<? extends BundleMessage> getHandler(short id) {
                return handlers.get(id);
            }            
        };
    }   
    
    private BundleFilter createBundleFilter(long account) {
        return new BundleFilter() {

            @Override
            public boolean filter(Transaction transaction,  boolean isConfirmed) {
                return transaction.getSenderId() == account || transaction.getRecipientId() == account;
            }            
        };
    }
}
