package com.makaranda.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "consultations")
public class Consultation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long clientUserId;
    private Long astrologerId;
    private Long clientProfileId;
    private LocalDateTime slotStart;
    private LocalDateTime slotEnd;
    @Enumerated(EnumType.STRING)
    private Mode mode = Mode.VIDEO;
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;
    private Integer amountInr;
    private String paymentRef;
    private String meetUrl;
    private String topic;
    private String notes;
    private Instant createdAt = Instant.now();

    public enum Mode { VIDEO, AUDIO, CHAT, IN_PERSON }
    public enum Status { PENDING, PAID, CONFIRMED, COMPLETED, CANCELLED, REFUNDED }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getClientUserId() { return clientUserId; }
    public void setClientUserId(Long clientUserId) { this.clientUserId = clientUserId; }
    public Long getAstrologerId() { return astrologerId; }
    public void setAstrologerId(Long astrologerId) { this.astrologerId = astrologerId; }
    public Long getClientProfileId() { return clientProfileId; }
    public void setClientProfileId(Long clientProfileId) { this.clientProfileId = clientProfileId; }
    public LocalDateTime getSlotStart() { return slotStart; }
    public void setSlotStart(LocalDateTime slotStart) { this.slotStart = slotStart; }
    public LocalDateTime getSlotEnd() { return slotEnd; }
    public void setSlotEnd(LocalDateTime slotEnd) { this.slotEnd = slotEnd; }
    public Mode getMode() { return mode; }
    public void setMode(Mode mode) { this.mode = mode; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Integer getAmountInr() { return amountInr; }
    public void setAmountInr(Integer amountInr) { this.amountInr = amountInr; }
    public String getPaymentRef() { return paymentRef; }
    public void setPaymentRef(String paymentRef) { this.paymentRef = paymentRef; }
    public String getMeetUrl() { return meetUrl; }
    public void setMeetUrl(String meetUrl) { this.meetUrl = meetUrl; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
