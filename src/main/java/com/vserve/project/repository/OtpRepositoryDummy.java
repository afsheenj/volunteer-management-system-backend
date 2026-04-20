package com.vserve.project.repository;

import com.vserve.project.entity.OtpDummy;
import com.vserve.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepositoryDummy extends JpaRepository<OtpDummy, Long> {

    @Query("""
        SELECT o
        FROM Otp o
        WHERE o.user = :user
        ORDER BY o.id DESC
        LIMIT 1
    """)
    OtpDummy recentOtp(User user);

//    Otp findTopByUserOrderByIdDesc(User user);
}
