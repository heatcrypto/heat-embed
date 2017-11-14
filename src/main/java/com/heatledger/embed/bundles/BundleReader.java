/*
 * Copyright (C) Heat Ledger Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Written by:
 *   - DM de Klerk <dennis@heatledger.com>, July 2017
 * */
package com.heatledger.embed.bundles;

import java.util.ArrayList;
import java.util.List;

import com.heatledger.Account;
import com.heatledger.Transaction;
import com.heatledger.Appendix.EncryptToSelfMessage;
import com.heatledger.Appendix.EncryptedMessage;
import com.heatledger.Appendix.Message;
import com.heatledger.crypto.Crypto;

public class BundleReader {

    String secretPhrase;
    long account;
    byte[] publicKey;
    byte[] privateKey;
    
    public BundleReader() {
        this(null);
    }
    
    public BundleReader(String secretPhrase) {
        if (secretPhrase != null) {
            this.secretPhrase = secretPhrase;
            this.publicKey = Crypto.getPublicKey(this.secretPhrase);
            this.account = Account.getId(this.publicKey);
            this.privateKey = Crypto.getPrivateKey(this.secretPhrase);
        }
    }
    
    public List<byte[]> getBytes(Transaction transaction) {
        byte[] senderPublicKey;
        if (privateKey != null)
            senderPublicKey = transaction.getSenderId() != account ? transaction.getSenderPublicKey() : publicKey;
        else
            senderPublicKey = null;
        return getBytesInternal(transaction, senderPublicKey);
    }    
    
    private List<byte[]> getBytesInternal(Transaction transaction, byte[] senderPublicKey) {
        List<byte[]> bytes = null;
        if (privateKey != null) {
            EncryptedMessage encryptedMessage = transaction.getEncryptedMessage();
            if (encryptedMessage != null && !encryptedMessage.isText()) {
                if (bytes == null) bytes = new ArrayList<byte[]>();
                bytes.add(encryptedMessage.getEncryptedData().decrypt(privateKey, senderPublicKey));
            }
            EncryptToSelfMessage encryptToSelfMessage = transaction.getEncryptToSelfMessage();
            if (encryptToSelfMessage != null && !encryptToSelfMessage.isText()) {
                if (bytes == null) bytes = new ArrayList<byte[]>();                
                bytes.add(encryptToSelfMessage.getEncryptedData().decrypt(privateKey, publicKey));
            }
        }
        Message message = transaction.getMessage();
        if (message != null && !message.isText()) {
            if (bytes == null) bytes = new ArrayList<byte[]>();               
            bytes.add(transaction.getMessage().getMessage());            
        }
        return bytes;
    }    
}
