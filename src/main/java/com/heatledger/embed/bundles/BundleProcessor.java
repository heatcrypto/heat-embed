package com.heatledger.embed.bundles;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.heatledger.Transaction;

public abstract class BundleProcessor {
    final static Logger log = LoggerFactory.getLogger(BundleProcessor.class);
    
    private BundleReader reader;
    private BundleHandlerResponseCollector responseCollector;
    private BundleDriver driver;
    
    public BundleProcessor(String secretPhrase) {
        this.reader = new BundleReader(secretPhrase);
        this.responseCollector = new BundleHandlerResponseCollector();
        this.driver = new BundleDriver(this.responseCollector);
    }
    
    public abstract BundleHandler<? extends BundleMessage> getHandler(short id);
    
    public BundleHandlerResponseCollector getResponseCollector() {
        return responseCollector;
    }
    
    public BundleDriver getDriver() {
        return driver;
    }
    
    public void processTransactions(List<? extends Transaction> transactions, boolean isConfirmed) {
        transactions.forEach(transaction -> {
            try {                
                processTransaction(transaction, isConfirmed);
            } catch (Exception e) {                
                e.printStackTrace();
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    private void processTransaction(Transaction transaction, boolean isConfirmed) throws Exception {
        List<byte[]> bytesList = reader.getBytes(transaction);
        if (bytesList != null) {
            for (byte[] bytes : bytesList) {
                if (bytes == null || bytes.length < Short.BYTES+1) {
                    continue;
                }
                
                ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
                short bundleId = buffer.getShort();
                BundleHandler<BundleMessage> handler = (BundleHandler<BundleMessage>) getHandler(bundleId);
                if (handler == null) {
                    log.info("bundle id not supported; "+bundleId);
                    continue;
                }
                if (!handler.filter(transaction, isConfirmed)) {
                    responseCollector.reportFilterFailed(transaction.getId(), bundleId);
                    log.info("filtered; bundle id "+bundleId+" transaction id "+Long.toUnsignedString(transaction.getId()));
                    continue;
                }        
                BundleMessage message = handler.getMessage(buffer);
                if (message == null) {
                    responseCollector.reportMessageNull(transaction.getId(), bundleId);
                    log.info("could not get message; bundle id "+bundleId+" transaction id "+Long.toUnsignedString(transaction.getId()));
                    continue;
                } 
                try {
                    handler.validate(transaction, message);
                    handler.apply(transaction, message);
                } catch (Exception e) {
                    responseCollector.reportException(transaction.getId(), bundleId, e);
                    throw e;
                }
                responseCollector.reportSuccess(transaction.getId(), bundleId);
            }
        }
    }    
}
