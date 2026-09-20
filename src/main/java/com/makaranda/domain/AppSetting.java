package com.makaranda.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "app_settings")
public class AppSetting {
    @Id
    private String keyName;
    @Lob
    private String valueJson;

    public AppSetting() {}
    public AppSetting(String keyName, String valueJson) {
        this.keyName = keyName;
        this.valueJson = valueJson;
    }
    public String getKeyName() { return keyName; }
    public void setKeyName(String keyName) { this.keyName = keyName; }
    public String getValueJson() { return valueJson; }
    public void setValueJson(String valueJson) { this.valueJson = valueJson; }
}
