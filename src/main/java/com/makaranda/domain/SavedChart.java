package com.makaranda.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "saved_charts")
public class SavedChart {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long clientProfileId;
    private String title;
    private LocalDateTime birthDateTime;
    private String place;
    private Double latitude;
    private Double longitude;
    private String ayanamsa;
    private String panchangMode;
    @Lob
    private String snapshotJson;
    private Instant createdAt = Instant.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getClientProfileId() { return clientProfileId; }
    public void setClientProfileId(Long clientProfileId) { this.clientProfileId = clientProfileId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public LocalDateTime getBirthDateTime() { return birthDateTime; }
    public void setBirthDateTime(LocalDateTime birthDateTime) { this.birthDateTime = birthDateTime; }
    public String getPlace() { return place; }
    public void setPlace(String place) { this.place = place; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getAyanamsa() { return ayanamsa; }
    public void setAyanamsa(String ayanamsa) { this.ayanamsa = ayanamsa; }
    public String getPanchangMode() { return panchangMode; }
    public void setPanchangMode(String panchangMode) { this.panchangMode = panchangMode; }
    public String getSnapshotJson() { return snapshotJson; }
    public void setSnapshotJson(String snapshotJson) { this.snapshotJson = snapshotJson; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
