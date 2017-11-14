package com.heatledger.embed.sample;

import java.nio.ByteBuffer;

import org.json.simple.JSONObject;

import com.heatledger.embed.bundles.AbstractBundleMessage;
import com.heatledger.embed.bundles.BundleUtil;

public class SampleMessageMessage extends AbstractBundleMessage {
    
    String contents;

    @Override
    public void read(ByteBuffer buffer) {
        contents = BundleUtil.readString(buffer);        
    }

    @Override
    public void write(ByteBuffer buffer) {
        BundleUtil.writeString(buffer, contents);        
    }

    @Override
    public void read(JSONObject json) {
        contents = (String) json.get("contents");              
    }

    @Override
    public void write(JSONObject json) {
        json.put("contents", contents);          
    }
}
