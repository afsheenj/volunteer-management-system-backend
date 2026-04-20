package com.vserve.project.entity;

import com.vserve.project.enums.ParticipationStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_participations",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"service_id", "user_id"})
        })
public class UserParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceRequest serviceRequest;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "organization_Id")
    private Organization organization; // nullable (only if org member)

    @Enumerated(EnumType.STRING)
    private ParticipationStatus status;

    private LocalDateTime appliedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime attendanceMarkedAt;
    private LocalDateTime withdrawalTime;

    private Double reliabilityScoreImpact;

    private Boolean feedbackGiven;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ServiceRequest getServiceRequest() {
        return serviceRequest;
    }

    public void setServiceRequest(ServiceRequest serviceRequest) {
        this.serviceRequest = serviceRequest;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public ParticipationStatus getStatus() {
        return status;
    }

    public void setStatus(ParticipationStatus status) {
        this.status = status;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public LocalDateTime getAttendanceMarkedAt() {
        return attendanceMarkedAt;
    }

    public void setAttendanceMarkedAt(LocalDateTime attendanceMarkedAt) {
        this.attendanceMarkedAt = attendanceMarkedAt;
    }

    public LocalDateTime getWithdrawalTime() {
        return withdrawalTime;
    }

    public void setWithdrawalTime(LocalDateTime withdrawalTime) {
        this.withdrawalTime = withdrawalTime;
    }

    public Double getReliabilityScoreImpact() {
        return reliabilityScoreImpact;
    }

    public void setReliabilityScoreImpact(Double reliabilityScoreImpact) {
        this.reliabilityScoreImpact = reliabilityScoreImpact;
    }

    public Boolean getFeedbackGiven() {
        return feedbackGiven;
    }

    public void setFeedbackGiven(Boolean feedbackGiven) {
        this.feedbackGiven = feedbackGiven;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
