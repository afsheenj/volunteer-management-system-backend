package com.vserve.project.repository;

import com.vserve.project.dto.user.UserAddressDto;
import com.vserve.project.entity.User;
import com.vserve.project.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress,Long> {
    boolean existsByUser(User user);
    UserAddress findByUserId(Long userId);
}
