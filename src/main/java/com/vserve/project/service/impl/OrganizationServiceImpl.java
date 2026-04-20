package com.vserve.project.service.impl;

import com.vserve.project.dto.organization.MemberDto;
import com.vserve.project.dto.organization.OrganizationMemberCreateDto;
import com.vserve.project.dto.organization.OrganizationRegisterRequestDto;
import com.vserve.project.dto.organization.OrganizationResponseDto;
import com.vserve.project.entity.Organization;
import com.vserve.project.entity.User;
import com.vserve.project.enums.AccountStatus;
import com.vserve.project.enums.Role;
import com.vserve.project.exception.BusinessException;
import com.vserve.project.repository.OrganizationRepository;
import com.vserve.project.repository.UserRepository;
import com.vserve.project.service.OrganizationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class OrganizationServiceImpl implements OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;

    public OrganizationServiceImpl(OrganizationRepository organizationRepository, UserRepository userRepository, JavaMailSender javaMailSender) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.javaMailSender = javaMailSender;
    }

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @Override
    public String registerOrganization(OrganizationRegisterRequestDto dto) {

        Organization existingOrg = organizationRepository.findByEmail(dto.email());

        if (existingOrg != null) {
            throw new BusinessException("Email already registered");
        }

        Organization existingPhone = organizationRepository.findByPhone(dto.phone());

        if (existingPhone != null) {
            throw new BusinessException("Phone already registered");
        }

        if (!dto.password().equals(dto.confirmPassword())) {
            throw new BusinessException("Passwords do not match");
        }

        Organization organization = mapToOrganization(dto);
        organizationRepository.save(organization);
        return "Organization registered successfully";
    }

    @Override
    public Page<OrganizationResponseDto> getAllRegisteredOrganizations(int page, int size, String sortBy, String order, String search, String status) {
        // 1. Create Sort object
        Sort sort = order.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

//        // 2. Create Pageable
//        Pageable pageable = PageRequest.of(page, size, sort);
//
//        // 3. Fetch paged entities and map to DTO
//        return organizationRepository.findAll(pageable)
//                .map(org -> new OrganizationResponseDto(
//                        org.getId(),
//                        org.getOrgName(),
//                        org.getEmail(),
//                        org.getPhone(),
//                        org.getStatus(),
//                        org.getMembers() != null ? org.getMembers().size() : 0
//                ));

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Organization> orgPage;

        AccountStatus accountStatus = null;

        if (status != null && !status.isBlank()) {
            try {
                accountStatus = AccountStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Invalid status");
            }
        }

        if (search != null && !search.isBlank() && accountStatus != null) {
             orgPage = organizationRepository.findWithFilters(
                    (search == null || search.isBlank()) ? null : search,
                    accountStatus,
                    pageable
            );
        } else if (search != null && !search.isBlank()) {
            orgPage = organizationRepository.findBySearchTerm(search, pageable);

        } else if (accountStatus != null) {
            orgPage = organizationRepository.findByStatus(accountStatus, pageable);

        } else {
            orgPage = organizationRepository.findAll(pageable);
        }

        return orgPage.map(org -> new OrganizationResponseDto(
                org.getId(),
                org.getOrgName(),
                org.getEmail(),
                org.getPhone(),
                org.getStatus(),
                org.getDescription(),
                org.getMembers() != null ? org.getMembers().size() : 0
        ));
    }

    @Override
    public String addMember(Long orgId, OrganizationMemberCreateDto dto) {

        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new BusinessException("Organization doesn't exist"));

        // Check duplicate email
        User existingUserByEmail = userRepository.findByEmail(dto.email());
        if (existingUserByEmail != null) {
            throw new BusinessException("User with this email already exists");
        }
        User existingByPhone = userRepository.findByPhone(dto.phone());
        if (existingByPhone != null) {
            throw new BusinessException("User with this phone already exists");
        }


        User user = createNewUser(organization, dto);
        userRepository.save(user);
        sendMailToOrganizationMember(user);

        return "Member was added successfully";
    }

    @Override
    public String updateOrganization(Long id,OrganizationRegisterRequestDto dto) {

        Organization organization=organizationRepository.findById(id).orElse(null);
        if(organization==null){
            throw new BusinessException("Organization not Found");
        }
        organization.setOrgName(dto.organizationName());
        organization.setEmail(dto.email());
        organization.setPhone(dto.phone());
        organization.setUpdatedAt(LocalDateTime.now());
        organization.setDescription(dto.description());
        organizationRepository.save(organization);
        return "Organization updated successfully";
    }

    @Override
    public Page<MemberDto> getAllMembers(
            Long orgId,
            int page,
            int size,
            String sortBy,
            String order,
            String search
    ) {

        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        Sort sort = order.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page,size,sort);

        Page<User> members;

        if(search != null && !search.isBlank()){
            members = userRepository.searchUsers(
                    organization,search,pageable
            );
//                    .findByOrganizationAndUsernameContainingIgnoreCaseOrOrganizationAndEmailContainingIgnoreCaseOrOrganizationAndPhoneContainingIgnoreCase(
//                            organization,search,
//                            organization,search,
//                            organization,search,
//                            pageable
//                    );
        }
        else{
            members = userRepository.findByOrganization(organization,pageable);
        }

        return members.map(this::mapToMemberDto);
    }

    private MemberDto mapToMemberDto(User user) {
        return new MemberDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone()
        );
    }

    @Override
    public String updateMemberDetails(Long memberId, OrganizationMemberCreateDto dto) {

        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        member.setUsername(dto.username());
        member.setEmail(dto.email());
        member.setPhone(dto.phone());

        userRepository.save(member);

        return "Member updated successfully";
    }

    @Override
    public String removeMember(Long memberId) {

        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        userRepository.delete(member);

        return "Member removed successfully";
    }

    private Organization mapToOrganization(OrganizationRegisterRequestDto dto) {

        Organization org = new Organization();
        org.setOrgName(dto.organizationName());
        org.setEmail(dto.email());
        org.setPhone(dto.phone());
        org.setPassword(passwordEncoder.encode(dto.password()));
        org.setStatus(AccountStatus.PENDING_VERIFICATION);
        org.setCreatedAt(LocalDateTime.now());
        org.setUpdatedAt(LocalDateTime.now());
        org.setDescription(dto.description());
        return org;
    }

    private User createNewUser(Organization organization, OrganizationMemberCreateDto dto){
        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPhone(dto.phone());
        user.setStatus(AccountStatus.PENDING_VERIFICATION);
        user.setRole(Role.ORGANIZATION_MEMBER);
        user.setOrganization(organization);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        String password = dto.username().substring(0,3) + "@" + dto.phone().substring(6);
        System.out.println(password);

        user.setPassword(passwordEncoder.encode(password));
        return user;
    }

    private void sendMailToOrganizationMember(User user) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Welcome to VServe - Organization Member Account Created");

        message.setText(
                "Hello " + user.getUsername() + ",\n\n" +

                        "Welcome to VServe!\n\n" +

                        "You have been successfully added as a member of the organization: "
                        + user.getOrganization().getOrgName() + ".\n\n" +

                        "Your account has been created with the following login details:\n" +
                        "Email: " + user.getEmail() + "\n\n" +

                        "Default Password Pattern:\n" +
                        "First 3 letters of your username + '@' + Last 4 digits of your registered phone number.\n\n" +

                        "Example Format: abc@1234\n\n" +

                        "For security reasons, we strongly recommend that you reset your password " +
                        "after your first login.\n\n" +

                        "If you have any questions, please contact your organization administrator.\n\n" +

                        "Best Regards,\n" +
                        "Team VServe"
        );

        javaMailSender.send(message);
    }

    @Override
    public String updateOrganizationStatus(Long id, String status) {

        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Organization not found"));

        AccountStatus accountStatus;

        try {
            accountStatus = AccountStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(
                    "Invalid status. Allowed: " + List.of(AccountStatus.values())
            );
        }

        org.setStatus(accountStatus);
        org.setUpdatedAt(LocalDateTime.now());

        organizationRepository.save(org);

        return "Status updated to " + accountStatus;
    }

    @Override
    public String deleteOrganization(Long id) {

        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Organization not found"));

        organizationRepository.delete(org);

        return "Organization deleted successfully";
    }
}
