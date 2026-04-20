package com.vserve.project.util;


import com.vserve.project.entity.Organization;
import com.vserve.project.entity.User;
import com.vserve.project.enums.AccountStatus;
import com.vserve.project.enums.DocumentStatus;
import com.vserve.project.exception.BusinessException;
import com.vserve.project.repository.DocumentVerificationRepository;
import com.vserve.project.repository.OrganizationRepository;
import com.vserve.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountValidator {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DocumentVerificationRepository documentVerificationRepository;
    @Autowired
    private OrganizationRepository organizationRepository;

    public User validateActiveUser(Long userId){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        boolean isApproved = documentVerificationRepository
                .existsByUserIdAndDocumentStatus(userId, DocumentStatus.APPROVED);

        if (isApproved && user.getStatus() != AccountStatus.ACTIVE) {
            user.setStatus(AccountStatus.ACTIVE);
            userRepository.save(user);
        }

        if(user.getStatus() != AccountStatus.ACTIVE){
            throw new BusinessException("Account not verified.");
        }

        return user;
    }

    public Organization validateActiveOrganization (Long organizationId){

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(()-> new BusinessException("Organization not found"));

        if(organization.getStatus() == AccountStatus.ACTIVE){
            organization.setStatus(AccountStatus.ACTIVE);
            organizationRepository.save(organization);
        }
        else {
            throw new BusinessException("Account not verified");
        }

        return organization;

    }
    
}