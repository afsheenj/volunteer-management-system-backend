package com.vserve.project.repository;

import com.vserve.project.entity.OrganizationAddress;
import com.vserve.project.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationAddressRepository extends JpaRepository<OrganizationAddress,Long> {
    OrganizationAddress findAllById(Long aLong);
    OrganizationAddress findByOrganizationId(Long organizationId);
}
