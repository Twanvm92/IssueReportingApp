package com.example.justin.verbeterjegemeente.data.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by twanv on 10-11-2017.
 */

@Entity(tableName = "service")
public class ServiceEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String service_code;
    private String service_name;
    private String description;
    private boolean metadeta;
    private String type;
    private String keywords;
    private String group;

    public ServiceEntry(int id, String service_code, String service_name, String description, boolean metadeta, String type, String keywords, String group) {
        this.id = id;
        this.service_code = service_code;
        this.service_name = service_name;
        this.description = description;
        this.metadeta = metadeta;
        this.type = type;
        this.keywords = keywords;
        this.group = group;
    }

    public String getService_code() {
        return service_code;
    }

    public String getService_name() {
        return service_name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isMetadeta() {
        return metadeta;
    }

    public String getType() {
        return type;
    }

    public String getKeywords() {
        return keywords;
    }

    public String getGroup() {
        return group;
    }

    public int getId() {
        return id;
    }
}
