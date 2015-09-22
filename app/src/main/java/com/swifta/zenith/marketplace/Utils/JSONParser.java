package com.swifta.zenith.marketplace.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by moyinoluwa on 14-Aug-15.
 */
public class JSONParser {
    private JSONObject data;

    public JSONParser(JSONObject jsonObject) {
        this.data = jsonObject;
    }

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

    public String toString() {
        return data.toString();
    }
}


