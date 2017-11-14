package com.heatledger.embed.sample;

import javax.xml.bind.annotation.XmlRootElement;

import org.json.simple.JSONObject;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

public class Models {

    @XmlRootElement(name = "Success")
    @ApiModel(value = "Success", description = "Success")        
    public static class JSONSuccess {
        
        @ApiModelProperty(value = "Success", required = true)
        public boolean success;        
        
        @SuppressWarnings("unchecked")
        public static JSONObject toJSON() {
            JSONObject json = new JSONObject();
            json.put("success", Boolean.TRUE);
            return json;
        }              
    }    
}
