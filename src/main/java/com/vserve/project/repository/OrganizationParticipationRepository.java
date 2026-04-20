package com.vserve.project.repository;

import com.vserve.project.entity.OrganizationParticipation;
import com.vserve.project.enums.ParticipationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface OrganizationParticipationRepository extends JpaRepository<OrganizationParticipation,Long> {

    boolean existsByServiceRequest_IdAndOrganization_Id(Long serviceId,Long organizationId);

    Page<OrganizationParticipation>
    findByOrganization_Id(Long organizationId, Pageable pageable);

    OrganizationParticipation findByServiceRequest_IdAndOrganization_Id(Long serviceId,Long organizationId);

//    OrganizationParticipation findByOrganization_Id(Long id);

    List<OrganizationParticipation> findByOrganization_Id(Long id);

    Page<OrganizationParticipation> findByServiceRequest_IdAndStatus(Long serviceId, ParticipationStatus participationStatus, Pageable pageable);

    Page<OrganizationParticipation> findByServiceRequest_Id(Long serviceId, Pageable pageable);

    List<OrganizationParticipation> findByServiceRequest_Id(Long serviceId);

    @Query("""
    SELECT op FROM OrganizationParticipation op
    WHERE op.organization.id = :orgId
    AND op.serviceRequest.serviceDate = :date
    AND op.serviceRequest.serviceStartTime = :time
    AND op.status NOT IN ('REJECTED','WITHDRAWN')
    """)
    List<OrganizationParticipation> findConflictingRegistrations(
            @Param("orgId") Long orgId,
            @Param("date") LocalDate date,
            @Param("time") LocalTime time
    );

    Page<OrganizationParticipation> findByOrganizationId(Long orgId, Pageable pageable);

    long countByServiceRequest_IdAndStatus(Long serviceId, ParticipationStatus participationStatus);

    long countByServiceRequest_Id(Long serviceId);


    @Modifying
    @Transactional
    @Query(value = """
        UPDATE organization_participations u
        LEFT JOIN service_requests s
        ON s.id = u.service_id
        SET u.status = 'REJECTED'
        WHERE u.status = 'REQUESTED'
            AND s.status = 'COMPLETED'
    """, nativeQuery = true)
    void changeParticipationStatus();


    @Query("""
SELECT AVG(o.reliabilityScoreImpact) 
FROM OrganizationParticipation o 
WHERE o.organization.id = :orgId
""")
    Double findScore(Long orgId);
}
