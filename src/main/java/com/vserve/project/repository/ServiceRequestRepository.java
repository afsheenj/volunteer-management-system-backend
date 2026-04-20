package com.vserve.project.repository;

import com.vserve.project.entity.ServiceRequest;
import com.vserve.project.enums.Category;
import com.vserve.project.enums.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

public interface ServiceRequestRepository
        extends JpaRepository<ServiceRequest, Long> {

    Page<ServiceRequest> findByStatus(RequestStatus status, Pageable pageable);

//    List<ServiceRequest> findByLocation(String landmark);

    List<ServiceRequest> findByCategory(Category category);

    Page<ServiceRequest> findByOrganizationId(Long organizationId, Pageable pageable);

    List<ServiceRequest> findByRequestedBy_Id(Long userId);

    Page<ServiceRequest> findByRequestedById(Long userId, Pageable pageable);

    List<ServiceRequest> findByOrganization_Id(Long organizationId);
    Page<ServiceRequest> findByStatusAndRequestedByIdNot(RequestStatus status, Long userId, Pageable pageable);




    @Modifying
    @Transactional
    @Query("""
        UPDATE ServiceRequest s
        SET s.status = 'IN_PROGRESS'
        WHERE s.serviceDate = :today
        AND s.serviceStartTime <= :time
        AND s.serviceEndTime >= :time
    """)
    void updateActiveServices(
            @Param("today") LocalDate today,
            @Param("time") LocalTime time);

    @Modifying
    @Transactional
    @Query("""
        UPDATE ServiceRequest s
        SET s.status = 'COMPLETED'
        WHERE s.serviceDate = :today
        AND s.serviceEndTime <= :time
    """)
    void updateCompleteServices(
            @Param("today") LocalDate today,
            @Param("time") LocalTime time);

    @Modifying
    @Transactional
    @Query("""
            UPDATE ServiceRequest s
            SET s.approvedCount = s.approvedCount + 1
            WHERE id = :id
            """)
    void updateApprovedCountNo(@Param("id") Long id);

    @Query(
            """
        
"""
    )
    List<ServiceRequest> findTop3ByOrderByCreatedAtDesc();

    @Query("""
    SELECT r FROM ServiceRequest r
    WHERE 
        (:category IS NULL OR r.category = :category)
        AND (:state IS NULL OR LOWER(r.state) LIKE LOWER(CONCAT('%', :state, '%')))
        AND (:city IS NULL OR LOWER(r.city) LIKE LOWER(CONCAT('%', :city, '%')))
""")
    Page<ServiceRequest> findWithFilters(
            @Param("category") Category category,
            @Param("state") String state,
            @Param("city") String city,
            Pageable pageable
    );

    long countByOrganizationIdAndStatus(Long orgId, RequestStatus status);

    long countByOrganizationIdAndServiceDateAfter(Long orgId, LocalDate date);
//    @Modifying
//    @Query("""
//        UPDATE ServiceRequest s
//        SET s.status = 'IN_PROGRESS'
//        WHERE s.serviceDate = :today
//        AND s.serviceStartTime >= :time
//        AND s.serviceEndTime <= :time
//    """)
//    void updateActiveServices(LocalDate today, LocalTime time);
//
//    @Modifying
//    @Query("""
//        UPDATE ServiceRequest s
//        SET s.status = 'COMPLETED'
//        WHERE s.serviceDate = :today
//        AND s.serviceEndTime >= :time
//    """)
//    void updateCompleteServices(LocalDate today, LocalTime time);

    @Query("""
        SELECT s FROM ServiceRequest s
        WHERE
        (:location IS NULL OR :location = '' OR
            LOWER(s.city) = LOWER(:location) OR LOWER(s.state) = LOWER(:location)
        )
    
        AND (:serviceType IS NULL OR :serviceType = '' OR
            LOWER(CAST(s.category AS string)) = LOWER(:serviceType)
        )
    
        AND (:date IS NULL OR s.serviceDate = :date)
    """)
    Page<ServiceRequest> searchRequests(
            @Param("location") String location,
            @Param("serviceType") String serviceType,
            @Param("date") LocalDate date,
            Pageable pageable
    );
}
