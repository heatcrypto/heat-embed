package com.heatledger.embed.sample;


public class SampleMessage {

    long id;
    int timestamp;
    long sender;
    long recipient;
    String contents;
    
    public SampleMessage(long id, int timestamp, long sender, long recipient, String contents) {
        this.id = id;
        this.timestamp = timestamp;
        this.sender = sender;
        this.recipient = recipient;
        this.contents = contents;
    }    
    
    public long getId() {
        return id;
    }
    
    public int getTimestamp() {
        return timestamp;
    }
    
    public long getSender() {
        return sender;
    }
    
    public long getRecipient() {
        return recipient;
    }
    
    public String getContents() {
        return contents;
    }
}
