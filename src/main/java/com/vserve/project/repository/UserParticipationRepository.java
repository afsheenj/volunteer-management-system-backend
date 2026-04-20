package com.vserve.project.repository;

import com.vserve.project.entity.UserParticipation;
import com.vserve.project.enums.ParticipationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface UserParticipationRepository extends JpaRepository<UserParticipation,Long> {
    boolean existsByServiceRequest_IdAndUser_Id(Long serviceId, Long userId);

    UserParticipation findByServiceRequest_IdAndUser_Id(Long serviceId, Long userId);

    List<UserParticipation> findByUser_Id(Long userId);

    Page<UserParticipation> findByServiceRequest_Id(Long serviceId, Pageable pageable);

    List<UserParticipation> findByServiceRequest_Id(Long serviceId);

    Page<UserParticipation> findByServiceRequest_IdAndStatus(Long serviceId, ParticipationStatus participationStatus, Pageable pageable);

    @Query("""
    SELECT up FROM UserParticipation up
    WHERE up.user.id = :userId
    AND up.serviceRequest.serviceDate = :date
    AND up.serviceRequest.serviceStartTime = :time
    AND up.status NOT IN ('REJECTED','WITHDRAWN')
    """)
    List<UserParticipation> findUserConflicts(
            @Param("userId") Long userId,
            @Param("date") LocalDate date,
            @Param("time") LocalTime time
    );

    Page<UserParticipation> findByUserId(Long userId, Pageable pageable);

    long countByServiceRequest_IdAndStatus(Long serviceId, ParticipationStatus participationStatus);

    long countByServiceRequest_Id(Long serviceId);

    @Modifying
    @Transactional
    @NativeQuery("""
        UPDATE user_participations u
        LEFT JOIN service_requests s
        ON s.id = u.service_id
        SET u.status = 'REJECTED'
        WHERE u.status = 'REQUESTED'
            AND s.status = 'COMPLETED'
    """)
    void changeParticipationStatus();

    @Query("""
        SELECT AVG(u.reliabilityScoreImpact) 
        FROM UserParticipation u 
        WHERE u.user.id = :userId
""")
    Double findScore(Long userId);
}
