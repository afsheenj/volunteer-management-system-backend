package com.vserve.project.repository;

import com.vserve.project.entity.Organization;
import com.vserve.project.entity.Otp;
import com.vserve.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends JpaRepository<Otp,Long> {

    @Query("""
        SELECT o
        FROM Otp o
        WHERE o.user = :user
        ORDER BY o.id DESC
        LIMIT 1
    """)
    Otp findLatestOtp(User user);

    @Query("""
        SELECT o
        FROM Otp o
        WHERE o.organization = :organization
        ORDER BY o.id DESC
        LIMIT 1
    """)
    Otp findLatestOrganizationOtp(Organization organization);
}
