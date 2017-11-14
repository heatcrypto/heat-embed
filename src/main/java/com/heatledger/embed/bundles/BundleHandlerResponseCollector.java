package com.heatledger.embed.bundles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.JSONObject;

public class BundleHandlerResponseCollector {
    
    public static class BundleHandlerResponse {
        public static final byte STATE_SUCCESS = (byte) 1;
        public static final byte STATE_FILTER_FAILED = (byte) 2;
        public static final byte STATE_MESSAGE_NULL = (byte) 3;
        public static final byte STATE_EXCEPTION = (byte) 4; 
        
        private short bundleId;
        private byte status;
        private Exception exception;
        public long transactionId;
        
        public BundleHandlerResponse(short bundleId) {
            this.transactionId = 0;
            this.bundleId = bundleId;
        }
        
        public BundleHandlerResponse(long transactionId, short bundleId) {
            this.transactionId = transactionId;
            this.bundleId = bundleId;
        }        
        
        public short getBundleId() {
            return bundleId;
        }
        
        public long getTransactionId() {
            return transactionId;
        }
        
        public byte getStatus() {
            return status;
        }
        
        public Exception getException() {
            return exception;
        }
        
        @SuppressWarnings("unchecked")
        public JSONObject toJSON() {
            JSONObject json = new JSONObject();
            json.put("bundleId", bundleId);
            json.put("success", status == STATE_SUCCESS);
            json.put("status", Byte.valueOf(status).intValue());
            if (exception != null) {
                json.put("error", exception.getMessage());
            }
            return json;
        }
    }    
    
    private Map<Long, List<BundleHandlerResponse>> map = new ConcurrentHashMap<Long, List<BundleHandlerResponse>>();
    
    public void startCollector(long transactionId) {
        map.put(transactionId, new ArrayList<>());
    }
    
    public void endCollector(long transactionId) {
        map.remove(transactionId);
    }    
    
    private BundleHandlerResponse getBundleHandlerResponse(long transactionId, short bundleId) {
        List<BundleHandlerResponse> responses = map.get(transactionId);
        if (responses != null) {
            for (BundleHandlerResponse response : responses) {
                if (response.bundleId == bundleId) {
                    return response;
                }
            }
            BundleHandlerResponse response = new BundleHandlerResponse(bundleId);
            responses.add(response);
            return response;
        }
        return null;
    }
    
    public void reportFilterFailed(long transactionId, short bundleId) {
        BundleHandlerResponse response = getBundleHandlerResponse(transactionId, bundleId);
        if (response != null) {
            response.status = BundleHandlerResponse.STATE_FILTER_FAILED;
        }
    }

    public void reportMessageNull(long transactionId, short bundleId) {
        BundleHandlerResponse response = getBundleHandlerResponse(transactionId, bundleId);
        if (response != null) {
            response.status = BundleHandlerResponse.STATE_MESSAGE_NULL;
        }
    }

    public void reportSuccess(long transactionId, short bundleId) {
        BundleHandlerResponse response = getBundleHandlerResponse(transactionId, bundleId);
        if (response != null) {
            response.status = BundleHandlerResponse.STATE_SUCCESS;
        }
    }
    
    public void reportException(long transactionId, short bundleId, Exception exception) {
        BundleHandlerResponse response = getBundleHandlerResponse(transactionId, bundleId);
        if (response != null) {
            response.status = BundleHandlerResponse.STATE_EXCEPTION;
            response.exception = exception;
        }
    }
    
    public List<BundleHandlerResponse> getResponses(long transactionId) {
        return map.get(transactionId);
    }
}
