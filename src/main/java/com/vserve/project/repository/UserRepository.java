package com.vserve.project.repository;

import com.vserve.project.entity.Organization;
import com.vserve.project.entity.User;
import com.vserve.project.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository <User, Long> {
    User findByEmail(String email);

    User findByPhone(String phone);

    long countByOrganizationId(Long organizationId);

    List<User> findByOrganizationId(Long organizationId);

    User findByUsername(String username);

    Page<User> findByOrganizationAndUsernameContainingIgnoreCaseOrOrganizationAndEmailContainingIgnoreCaseOrOrganizationAndPhoneContainingIgnoreCase(
            Organization organization1,String username,
            Organization organization2,String email,
            Organization organization3,String phone,
            Pageable pageable
    );

    @Query("""
    SELECT u FROM User u
    WHERE u.organization = :organization
    AND (
        LOWER(u.username) LIKE LOWER(CONCAT(:search, '%')) OR
        LOWER(u.email) LIKE LOWER(CONCAT(:search, '%')) OR
        LOWER(u.phone) LIKE LOWER(CONCAT(:search, '%'))
    )
    """)
    Page<User> searchUsers(Organization organization, String search, Pageable pageable);

    Page<User> findByOrganization(Organization organization, Pageable pageable);


//    List<User> findByRoleAndAddress_CityAndStatus(
//            String role,
//            String city,
//            String status
//    );

    List<User> findTop6ByOrderByCreatedAtDesc();



    Page<User> findByUsernameContainingIgnoreCaseAndRoleAndAvailability(String username, Role roleEnum, Boolean availability, Pageable pageable);

    Page<User> findByUsernameContainingIgnoreCaseAndRole(String username, Role roleEnum, Pageable pageable);

    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    Page<User> findByRole(Role roleEnum, Pageable pageable);

    Page<User> findByAvailability(Boolean availability, Pageable pageable);

    List<User> findByRole(Role role);

    @Query("""
    SELECT ua.user FROM UserAddress ua
    WHERE ua.state = :state
    AND ua.city = :city
    AND ua.user.role IN :roles
""")
    List<User> findUsersByLocationAndRoles(
            @Param("state") String state,
            @Param("city") String city,
            @Param("roles") List<Role> roles
    );

}
