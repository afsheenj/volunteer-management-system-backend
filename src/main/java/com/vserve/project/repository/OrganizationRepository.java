package com.vserve.project.repository;

import com.vserve.project.entity.Organization;
import com.vserve.project.enums.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization,Long> {
    Organization findByEmail(String email);

    Organization findByPhone(String phone);

    Page<Organization> findAll(Pageable pageable);

    @Query("SELECT o FROM Organization o WHERE LOWER(o.orgName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Organization> findBySearchTerm(@Param("search") String search, Pageable pageable);


    Optional<Organization> findByOrgName(String name);

    List<Organization> findTop6ByOrderByCreatedAtDesc();

    @Query("""
    SELECT o FROM Organization o
    WHERE 
        (:search IS NULL OR LOWER(o.orgName) LIKE LOWER(CONCAT('%', :search, '%')))
        AND (:status IS NULL OR o.status = :status)
""")
    Page<Organization> findWithFilters(
            @Param("search") String search,
            @Param("status") AccountStatus status,
            Pageable pageable
    );
    Page<Organization> findByStatus(AccountStatus accountStatus, Pageable pageable);

    @Query("""
    SELECT oa.organization FROM OrganizationAddress oa
    WHERE oa.state = :state
    AND oa.city = :city
""")
    List<Organization> findOrganizationsByLocation(
            @Param("state") String state,
            @Param("city") String city
    );

    @Query("SELECT COUNT(u) FROM User u WHERE u.organization.id = :orgId")
    Integer findMemberCount(Long orgId);
}
