package com.heatledger.embed.bundles;

import java.util.List;

import com.heatledger.Account;
import com.heatledger.Appendix.Message;
import com.heatledger.Appendix.PublicKeyAnnouncement;
import com.heatledger.Attachment;
import com.heatledger.Heat;
import com.heatledger.Transaction;
import com.heatledger.Transaction.Builder;
import com.heatledger.api.APIException;
import com.heatledger.crypto.Crypto;
import com.heatledger.embed.bundles.BundleHandlerResponseCollector.BundleHandlerResponse;

public class BundleDriver {
    
    private final BundleHandlerResponseCollector responseCollector;
    
    public BundleDriver(BundleHandlerResponseCollector responseCollector) {
        this.responseCollector = responseCollector;
    }
   
    public long sendMessage(String secretPhrase, long recipientId, byte[] recipientPublicKey, short bundleId, BundleMessage message) throws Exception {
        byte[] messageBytes = BundleUtil.toArray(bundleId, message);
        List<BundleHandlerResponse> responses = sendMessage(secretPhrase, recipientId, recipientPublicKey, messageBytes);
        for (BundleHandlerResponse response : responses) {
            if (response.getBundleId() == bundleId) {
                switch (response.getStatus()) {
                case BundleHandlerResponse.STATE_SUCCESS:
                    return response.getTransactionId();
                case BundleHandlerResponse.STATE_FILTER_FAILED:
                    throw new RuntimeException("filter failed");
                case BundleHandlerResponse.STATE_MESSAGE_NULL:
                    throw new RuntimeException("message null");
                case BundleHandlerResponse.STATE_EXCEPTION:
                    throw response.getException();
                }
            }
        }
        throw new RuntimeException("not reached [bundle driver]");
    }
    
    private List<BundleHandlerResponse> sendMessage(String secretPhrase, long recipientId, byte[] recipientPublicKey, byte[] message) {
        byte[] senderPublicKey = Crypto.getPublicKey(secretPhrase);
        long amountHQT = 0;
        long feeHQT = 0;
        short deadline = 33;
        Attachment attachment = Attachment.ARBITRARY_MESSAGE;
        Builder builder = Heat.newTransactionBuilder(senderPublicKey, amountHQT, feeHQT, deadline, attachment);
        if (recipientId == 0 && recipientPublicKey != null) {
            recipientId = Account.getId(recipientPublicKey);
        }
        if (recipientId == 0) {
            throw new RuntimeException("Missing recipient and recipient public key");
        }
        builder.recipientId(recipientId); 
        if (recipientPublicKey != null) {
            builder.publicKeyAnnouncement(new PublicKeyAnnouncement(recipientPublicKey));
        }
        builder.message(new Message(message));
        return buildAndBroadCast(secretPhrase, builder);
    }
    
    private List<BundleHandlerResponse> buildAndBroadCast(String secretPhrase, Builder builder) {
        try {
            Transaction transaction = null;
            try {
                transaction = builder.build();
                transaction.sign(secretPhrase);                
                responseCollector.startCollector(transaction.getId());      // START COLLECTOR
                Heat.getTransactionProcessor().broadcast(transaction);
                List<BundleHandlerResponse> responses = responseCollector.getResponses(transaction.getId());
                for (BundleHandlerResponse resp : responses) {
                    resp.transactionId = transaction.getId();
                }
                return responses;
            } finally {
                responseCollector.endCollector(transaction.getId());        // END COLLECTOR
            }            
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.error(e.toString());
        }        
    }
}
