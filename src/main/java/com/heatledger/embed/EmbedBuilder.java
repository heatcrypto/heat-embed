package com.heatledger.embed;

import java.io.File;
import java.util.Properties;

import com.heatledger.Heat.ExternalAPIAdditions;
import com.heatledger.Heat.ExternalReplicatorAdditions;
import com.heatledger.util.Convert;

public class EmbedBuilder {

    String genesisSecretPhrase = null;
    byte[] genesisPublicKey = null;
    Properties properties = null;
    String embedName;
    File blockchainSeed;
    boolean rescan = false;
    boolean rescanAndValidate = false;
    ExternalReplicatorAdditions externalReplicatorAdditions = null;
    ExternalAPIAdditions externalAPIAdditions = null;
    
    public EmbedBuilder(Class<?> clazz) {
        embedName = clazz.getName();
    }

    public Embed build() {
        return new Embed(this);
    }
    
    public EmbedBuilder genesisSecretPhrase(String genesisSecretPhrase) {
        this.genesisSecretPhrase = genesisSecretPhrase;
        return this;
    }
    
    public EmbedBuilder genesisPublicKey(String genesisPublickeyHex) {
        this.genesisPublicKey = Convert.parseHexString(genesisPublickeyHex);
        return this;
    }
    
    public EmbedBuilder properties(Properties properties) {
        this.properties = properties;
        return this;
    }
    
    public EmbedBuilder blockchainSeed(File blockchainSeed) {
        this.blockchainSeed = blockchainSeed;
        return this;
    }
    
    public EmbedBuilder rescan(boolean rescan) {
        this.rescan = rescan;
        return this;
    }
    
    public EmbedBuilder rescanAndValidate(boolean rescanAndValidate) {
        this.rescanAndValidate = rescanAndValidate;
        return this;
    }
    
    public EmbedBuilder externalReplicatorAdditions(ExternalReplicatorAdditions externalReplicatorAdditions) {
        this.externalReplicatorAdditions = externalReplicatorAdditions;
        return this;
    }

    public EmbedBuilder externalAPIAdditions(ExternalAPIAdditions externalAPIAdditions) {
        this.externalAPIAdditions = externalAPIAdditions;
        return this;
    }    
}
