package com.qrvault.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "qr_history")
public class QRHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String qrType;   // URL, TEXT, WIFI, CONTACT

    @Column(nullable = false, length = 1000)
    private String inputData;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // Constructors
    public QRHistory() {}

    public QRHistory(User user, String qrType, String inputData) {
        this.user = user;
        this.qrType = qrType;
        this.inputData = inputData;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getQrType() { return qrType; }
    public void setQrType(String qrType) { this.qrType = qrType; }

    public String getInputData() { return inputData; }
    public void setInputData(String inputData) { this.inputData = inputData; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
