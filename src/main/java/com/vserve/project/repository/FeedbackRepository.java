package com.vserve.project.repository;

import com.vserve.project.entity.Feedback;
import com.vserve.project.entity.OrganizationParticipation;
import com.vserve.project.entity.UserParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback,Long> {

    List<Feedback> findByUserParticipation_Id(Long userParticipationId);

    List<Feedback> findByOrganizationParticipation_Id(Long organizationParticipationId);

    List<Feedback> findByGivenByUser_Id(Long userId);

    List<Feedback> findByGivenByOrganization_Id(Long organizationId);

    Optional<Feedback> findByUserParticipation(UserParticipation participation);

    Optional<Feedback> findByOrganizationParticipation(OrganizationParticipation participation);
}
