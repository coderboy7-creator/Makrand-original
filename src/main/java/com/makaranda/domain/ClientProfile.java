package com.makaranda.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "clients")
public class ClientProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long ownerUserId;
    private Long astrologerId;
    private String name;
    private String gender;
    private String email;
    private String phone;
    private String notes;
    private LocalDateTime birthDateTime;
    private String timeZone = "Asia/Kolkata";
    private Double tzOffsetHours = 5.5;
    private Double latitude = 26.5833;
    private Double longitude = 85.268;
    private String place = "Darbhanga, Bihar, India (KSDS अक्षांश २६।३५)";
    private String ayanamsa = "SURYA_SIDDHANTA_MAKARANDA";
    private String panchangMode = "SIDDHANTIC";
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOwnerUserId() { return ownerUserId; }
    public void setOwnerUserId(Long ownerUserId) { this.ownerUserId = ownerUserId; }
    public Long getAstrologerId() { return astrologerId; }
    public void setAstrologerId(Long astrologerId) { this.astrologerId = astrologerId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getBirthDateTime() { return birthDateTime; }
    public void setBirthDateTime(LocalDateTime birthDateTime) { this.birthDateTime = birthDateTime; }
    public String getTimeZone() { return timeZone; }
    public void setTimeZone(String timeZone) { this.timeZone = timeZone; }
    public Double getTzOffsetHours() { return tzOffsetHours; }
    public void setTzOffsetHours(Double tzOffsetHours) { this.tzOffsetHours = tzOffsetHours; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getPlace() { return place; }
    public void setPlace(String place) { this.place = place; }
    public String getAyanamsa() { return ayanamsa; }
    public void setAyanamsa(String ayanamsa) { this.ayanamsa = ayanamsa; }
    public String getPanchangMode() { return panchangMode; }
    public void setPanchangMode(String panchangMode) { this.panchangMode = panchangMode; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
