package com.vserve.project.entity;

import com.vserve.project.enums.Category;
import com.vserve.project.enums.RequestStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "service_requests")
public class ServiceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String landmark;
    private String state;
    private String city;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private String requestType;

    private LocalDate serviceDate;
    private LocalTime serviceStartTime;
    private LocalTime serviceEndTime;

    private Integer minVolunteers;
    private Integer maxVolunteers;

    @ManyToOne
    @JoinColumn(name = "requested_by_user_id")
    private User requestedBy;

    @ManyToOne
    @JoinColumn(name = "organization_Id")
    private Organization organization;

    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Integer registeredCount = 0;

    @Column(nullable = false)
    private Integer approvedCount = 0;

    private LocalDateTime updatedAt;

    @Column(columnDefinition = "LONGTEXT")
    private String embeddingVector;

    public Integer getRegisteredCount() {
        return registeredCount;
    }

    public void setRegisteredCount(Integer registeredCount) {
        this.registeredCount = registeredCount;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public User getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(User requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public LocalDate getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(LocalDate serviceDate) {
        this.serviceDate = serviceDate;
    }

    public LocalTime getServiceStartTime() {
        return serviceStartTime;
    }

    public void setServiceStartTime(LocalTime serviceStartTime) {
        this.serviceStartTime = serviceStartTime;
    }

    public Integer getMinVolunteers() {
        return minVolunteers;
    }

    public void setMinVolunteers(Integer minVolunteers) {
        this.minVolunteers = minVolunteers;
    }

    public Integer getMaxVolunteers() {
        return maxVolunteers;
    }

    public void setMaxVolunteers(Integer maxVolunteers) {
        this.maxVolunteers = maxVolunteers;
    }

    public LocalTime getServiceEndTime() {
        return serviceEndTime;
    }

    public void setServiceEndTime(LocalTime serviceEndTime) {
        this.serviceEndTime = serviceEndTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getApprovedCount() {
        return approvedCount;
    }

    public void setApprovedCount(Integer approvedCount) {
        this.approvedCount = approvedCount;
    }

    public String getEmbeddingVector() {
        return embeddingVector;
    }

    public void setEmbeddingVector(String embeddingVector) {
        this.embeddingVector = embeddingVector;
    }

    @Override
    public String toString() {

        String name = "";
        if (organization != null)
           name = organization.getOrgName();

        return "ServiceRequest{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category=" + category +
                ", landmark='" + landmark + '\'' +
                ", state='" + state + '\'' +
                ", city='" + city + '\'' +
                ", status=" + status +
                ", requestType='" + requestType + '\'' +
                ", serviceDate=" + serviceDate +
                ", serviceStartTime=" + serviceStartTime +
                ", serviceEndTime=" + serviceEndTime +
                ", minVolunteers=" + minVolunteers +
                ", maxVolunteers=" + maxVolunteers +
                ", requestedBy=" + requestedBy +
                ", organization=" + organization +
                ", organization name=" + name +
                ", createdAt=" + createdAt +
                ", registeredCount=" + registeredCount +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
