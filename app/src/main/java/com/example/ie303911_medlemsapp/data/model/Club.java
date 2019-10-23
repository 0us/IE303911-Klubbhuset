package com.example.ie303911_medlemsapp.data.model;

import org.json.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Club {
    private long oid;
    private String name;

    public Club(JSONObject json) {
        // TODO: implement
    }
}
