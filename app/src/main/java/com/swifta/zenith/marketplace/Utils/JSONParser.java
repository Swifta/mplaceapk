package com.swifta.zenith.marketplace.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


/**
 * Created by moyinoluwa on 14-Aug-15.
 */
public class JSONParser implements Serializable {
    private JSONObject data;

    public JSONParser(JSONObject jsonObject) {
        this.data = jsonObject;
    }

    /**
     * Retrieves data from the JSONObject based on the specified key
     */
    public Object getProperty(String key) {
        try {
            if (data.has(key)) {
                return data.get(key);
            } else {
                return "Not found";
            }
        } catch (JSONException exception) {
            return "Not found";
        }
    }

    /**
     * Converts the JSONObect object to a string
     */
    public String toString() {
        return data.toString();
    }
}


