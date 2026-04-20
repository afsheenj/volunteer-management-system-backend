package com.vserve.project.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_participation_id")
    private UserParticipation userParticipation;

    @ManyToOne
    @JoinColumn(name = "organization_participation_id")
    private OrganizationParticipation organizationParticipation;

    @ManyToOne
    @JoinColumn(name = "given_by_user_id")
    private User givenByUser;

    @ManyToOne
    @JoinColumn(name = "given_by_org_id")
    private Organization givenByOrganization;

    private Integer rating;

    private String comment;

    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserParticipation getUserParticipation() {
        return userParticipation;
    }

    public void setUserParticipation(UserParticipation userParticipation) {
        this.userParticipation = userParticipation;
    }

    public OrganizationParticipation getOrganizationParticipation() {
        return organizationParticipation;
    }

    public void setOrganizationParticipation(OrganizationParticipation organizationParticipation) {
        this.organizationParticipation = organizationParticipation;
    }

    public User getGivenByUser() {
        return givenByUser;
    }

    public void setGivenByUser(User givenByUser) {
        this.givenByUser = givenByUser;
    }

    public Organization getGivenByOrganization() {
        return givenByOrganization;
    }

    public void setGivenByOrganization(Organization givenByOrganization) {
        this.givenByOrganization = givenByOrganization;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}