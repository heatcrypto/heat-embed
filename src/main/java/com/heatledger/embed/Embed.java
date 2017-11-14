package com.heatledger.embed;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.heatledger.Block;
import com.heatledger.BlockchainProcessor;
import com.heatledger.BlockchainProcessorImpl;
import com.heatledger.Heat;
import com.heatledger.util.Listener;

public class Embed {
    final static Logger log = LoggerFactory.getLogger(BlockchainProcessorImpl.class);

    private EmbedBuilder builder;
    private List<Runnable> readyRunnables = new ArrayList<Runnable>();
    private boolean isReady = false;
    private Listener<Block> onReady;

    public Embed(EmbedBuilder builder) {
        this.builder = builder;
    }

    public void start(String[] args) {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("Embed shutdown hook triggered");
                        shutdown();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }));
            init(args);
        } catch (Throwable t) {
            System.out.println("Fatal error: " + t.toString() + "\n" + ExceptionUtils.getRootCauseMessage(t));
        }
    }
    
    public void ready(Runnable runnable) {
        if (isReady) {
            runnable.run();
        }
        else {
            readyRunnables.add(runnable);
        }
    }
    
    public void exit() {
        try {
            Heat.shutdown();
            Heat.shutdownJvmFromEclipse();
        }
        catch (IOException e) {
            e.printStackTrace();
        }        
    }
    
    public void scan(boolean validate, Listener<Block> listener) {
        ready(() -> {
            Heat.getBlockchainProcessor().addListener(listener, BlockchainProcessor.Event.BLOCK_SCANNED);           
            BlockchainProcessorImpl processor = (BlockchainProcessorImpl) Heat.getBlockchainProcessor();
            processor.scan(0, validate);
            Heat.getBlockchainProcessor().removeListener(listener, BlockchainProcessor.Event.BLOCK_SCANNED);
        });
    }    
    
    public void scan(Listener<Block> listener) {
        scan(false, listener);
    }

    private void init(String[] args) {

        String name = builder.embedName;
        String artifactsDir = name + "_artifacts";
        String dbDir = name + "_db";
        String blockchainDir = name + "_blockchain";
        
        log.debug("Init embed {}", name);
        log.debug("artifactsDir  = {}", artifactsDir);
        log.debug("dbDir         = {}", dbDir);
        log.debug("blockchainDir = {}", blockchainDir);
        
        Properties properties = new Properties();
        
        properties.setProperty("heat.replicatorArtifactDir",artifactsDir);
        properties.setProperty("heat.blockchainDir",blockchainDir);
        properties.setProperty("heat.replicatorJdbcUrlH2", "jdbc:h2:./"+dbDir+"/blocktech_replicator;DB_CLOSE_ON_EXIT=FALSE;MVCC=FALSE;MV_STORE=FALSE;MODE=MYSQL");

        if (builder.properties != null) {
            properties.putAll(builder.properties);
        }
        
        if (builder.blockchainSeed != null) {
            try {
                setupBlockchainSeed(artifactsDir, dbDir, blockchainDir);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }        
        
        initGenesis(properties);                      
        Heat.init(args, properties, builder.externalAPIAdditions, builder.externalReplicatorAdditions,
                () -> {
                    
                    onReady = block -> {
                        isReady = true;
                        readyRunnables.forEach(runnable -> runnable.run());
                        readyRunnables.clear();
                        Heat.getBlockchainProcessor().removeListener(onReady, BlockchainProcessor.Event.BLOCKCHAIN_READY);
                    };        
                    Heat.getBlockchainProcessor().addListener(onReady, BlockchainProcessor.Event.BLOCKCHAIN_READY);                    
                    
                    overrideConstants();
                    overrideFees();
                    overridePeer2Peer();                      
                });
    }    
    
    private void setupBlockchainSeed(String artifactsDir, String dbDir, String blockchainDir) throws IOException {
        File artifactsDirFile = new File(artifactsDir);
        if (artifactsDirFile.exists()) {
            log.debug("Deleting {}", artifactsDirFile.getPath());
            FileUtils.deleteDirectory(artifactsDirFile);
        }
        
        File dbDirFile = new File(dbDir);
        if (dbDirFile.exists()) {
            log.debug("Deleting {}", dbDirFile.getPath());
            FileUtils.deleteDirectory(dbDirFile);
        }
        
        File blockchainDirFile = new File(blockchainDir);
        if (blockchainDirFile.exists()) {
            log.debug("Deleting {}", blockchainDirFile.getPath());
            FileUtils.deleteDirectory(blockchainDirFile);
        }        
        
        log.debug("Copy {} to {}", builder.blockchainSeed, blockchainDirFile.getPath());        
        FileUtils.copyDirectory(FileUtils.getFile(builder.blockchainSeed), FileUtils.getFile(blockchainDirFile));
        log.debug("Done copying {} to {}", builder.blockchainSeed, blockchainDirFile.getPath());
        
        log.debug("Force unlock persist stores");
        
        FileReader reader = new FileReader(FileUtils.getFile(blockchainDirFile, "store"));        
        Properties store = new Properties();
        store.load(reader);
        
        /* sets each persist key/value store locked property to false,
         * there is a mild risk here that a store got corrupted since it was not
         * shutdown correctly
         * not doing this should make things considerably more difficult since the 
         * heat core will do a shutdown after it has restored each store */
        store.forEach((key, value) -> {
            if (((String)key).startsWith("locked") && "true".equals(value)) {
                log.debug("Unlocking {}", key);
                store.setProperty((String)key, "false");
            }
        });
        
        store.setProperty("scan.validate", "false");
        store.setProperty("scan.rescan", "false");
        
        if (builder.rescanAndValidate) {
            store.setProperty("scan.rescan", "true");            
            store.setProperty("scan.validate", "true");            
        }
        else if (builder.rescan) {
            store.setProperty("scan.rescan", "true");            
            store.setProperty("scan.validate", "false");            
        }
        
        FileWriter writer = new FileWriter(FileUtils.getFile(blockchainDirFile, "store"));        
        store.store(writer, null);       
        
        log.debug("Done force unlocking persist stores");        
    }

    private void initGenesis(Properties properties) {}

    protected void shutdown() throws IOException {
        Heat.shutdown();        
    }
    
    private void overrideConstants() {}

    private void overrideFees() {}

    private void overridePeer2Peer() {}   
}
