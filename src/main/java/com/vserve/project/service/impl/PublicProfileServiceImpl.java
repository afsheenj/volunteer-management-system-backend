package com.vserve.project.service.impl;

import com.vserve.project.dto.PublicParticipationDto;
import com.vserve.project.dto.PublicRequestDto;
import com.vserve.project.dto.user.UserSkillResponseDto;
import com.vserve.project.dto.PublicProfileDto;
import com.vserve.project.entity.*;
import com.vserve.project.repository.*;
import com.vserve.project.service.OrganizationProfileService;
import com.vserve.project.service.PublicProfileService;
import com.vserve.project.service.UserProfileService;
import com.vserve.project.service.UserSkillService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PublicProfileServiceImpl implements PublicProfileService {

    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationAddressRepository organizationAddressRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final UserParticipationRepository userParticipationRepository;
    private final OrganizationParticipationRepository organizationParticipationRepository;

    private final UserProfileService userProfileService;
    private final UserSkillService userSkillService;
    private final OrganizationProfileService organizationProfileService;



    public PublicProfileServiceImpl(
            UserRepository userRepository, UserAddressRepository userAddressRepository,
            OrganizationRepository organizationRepository, OrganizationAddressRepository organizationAddressRepository, ServiceRequestRepository serviceRequestRepository, UserParticipationRepository userParticipationRepository, OrganizationParticipationRepository organizationParticipationRepository,
            UserProfileService userProfileService,
            UserSkillService userSkillService,
            OrganizationProfileService organizationProfileService) {

        this.userRepository = userRepository;
        this.userAddressRepository = userAddressRepository;
        this.organizationRepository = organizationRepository;
        this.organizationAddressRepository = organizationAddressRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.userParticipationRepository = userParticipationRepository;
        this.organizationParticipationRepository = organizationParticipationRepository;
        this.userProfileService = userProfileService;
        this.userSkillService = userSkillService;
        this.organizationProfileService = organizationProfileService;

    }

    public PublicProfileDto getPublicProfile(String name) {

        Optional<User> userOpt = Optional.ofNullable(userRepository.findByUsername(name));

        if(userOpt.isPresent()){

            User user = userOpt.get();
            UserAddress userAddress = userAddressRepository.findByUserId(user.getId());
            String city = (userAddress == null)? "-" : userAddress.getCity();
            String state = (userAddress == null)? "-" : userAddress.getState();
            Double score = userParticipationRepository.findScore(user.getId());
            // skills
            List<UserSkill> userSkills =
                    userSkillService.getUserSkills(user.getId());

            List<UserSkillResponseDto> skills =
                    userSkills.stream()
                            .map(skill -> new UserSkillResponseDto(
                                    skill.getId(),
                                    skill.getSkill().getSkillName(),
                                    skill.getProficiencyLevel()
                            ))
                            .toList();

            // requests created by user
            List<ServiceRequest> requests =
                    serviceRequestRepository.findByRequestedBy_Id(user.getId());
            System.out.println("requests = " + requests);
            System.out.println("SErvice REquest "+ requests);
            List<PublicRequestDto> postedRequests =
                    requests.stream()
                            .map(r -> new PublicRequestDto(
                                    r.getId(),
                                    r.getTitle(),
                                    r.getCategory().name(),
                                    r.getStatus().name(),
                                    r.getState(),
                                    r.getCity()
                            ))
                            .toList();

            // requests user participated in
            List<UserParticipation> participations =
                    userParticipationRepository.findByUser_Id(user.getId());

            List<PublicParticipationDto> participationDtos =
                    participations.stream()
                            .map(p -> new PublicParticipationDto(
                                    p.getServiceRequest().getId(),
                                    p.getServiceRequest().getTitle(),
                                    p.getStatus().name()
                            ))
                            .toList();

            return new PublicProfileDto(
                    "USER",
                    user.getUsername(),
                    city,
                    state,
                    skills,
                    postedRequests,
                    participationDtos,
                    score
            );
        }

        Optional<Organization> orgOpt =
                organizationRepository.findByOrgName(name);

        if(orgOpt.isPresent()){

            Organization org = orgOpt.get();
            OrganizationAddress organizationAddress = organizationAddressRepository.findByOrganizationId(org.getId());

            String city = (organizationAddress == null)? "-":organizationAddress.getCity();
            String state = (organizationAddress == null)? "-":organizationAddress.getState();
            Double score = organizationParticipationRepository.findScore(org.getId());
            List<ServiceRequest> requests =
                     serviceRequestRepository.findByOrganization_Id(org.getId());
            System.out.println("SErvice REquest "+ requests);
            List<PublicRequestDto> postedRequests =
                    requests.stream()
                            .map(r -> new PublicRequestDto(
                                    r.getId(),
                                    r.getTitle(),
                                    r.getCategory().name(),
                                    r.getStatus().name(),
                                    r.getState(),
                                    r.getCity()))
                            .toList();
            List<OrganizationParticipation> participations =
                    organizationParticipationRepository.findByOrganization_Id(org.getId());

            List<PublicParticipationDto> participationDtos =
                    participations.stream()
                            .map(p -> new PublicParticipationDto(
                                    p.getServiceRequest().getId(),
                                    p.getServiceRequest().getTitle(),
                                    p.getStatus().name()
                            ))
                            .toList();

            return new PublicProfileDto(
                    "ORGANIZATION",
                    org.getOrgName(),
                    city,
                    state,
                    List.of(),
                    postedRequests,
                    participationDtos,
                    score
            );
        }

        throw new RuntimeException("Profile not found");
    }

    @Override
    public PublicProfileDto getPublicProfileById(Long id) {

        Optional<User> userOpt = userRepository.findById(id);

        if(userOpt.isPresent()){

            User user = userOpt.get();
            UserAddress userAddress = userAddressRepository.findByUserId(user.getId());
            String city = (userAddress == null)? "-" : userAddress.getCity();
            String state = (userAddress == null)? "-" : userAddress.getState();
            Double score = userParticipationRepository.findScore(user.getId());
            // skills
            List<UserSkill> userSkills =
                    userSkillService.getUserSkills(user.getId());

            List<UserSkillResponseDto> skills =
                    userSkills.stream()
                            .map(skill -> new UserSkillResponseDto(
                                    skill.getId(),
                                    skill.getSkill().getSkillName(),
                                    skill.getProficiencyLevel()
                            ))
                            .toList();

            // requests created by user
            List<ServiceRequest> requests =
                    serviceRequestRepository.findByRequestedBy_Id(user.getId());
            List<PublicRequestDto> postedRequests =
                    requests.stream()
                            .map(r -> new PublicRequestDto(
                                    r.getId(),
                                    r.getTitle(),
                                    r.getCategory().name(),
                                    r.getStatus().name(),
                                    r.getState(),
                                    r.getCity()
                            ))
                            .toList();

            // requests user participated in
            List<UserParticipation> participations =
                    userParticipationRepository.findByUser_Id(user.getId());

            List<PublicParticipationDto> participationDtos =
                    participations.stream()
                            .map(p -> new PublicParticipationDto(
                                    p.getServiceRequest().getId(),
                                    p.getServiceRequest().getTitle(),
                                    p.getStatus().name()
                            ))
                            .toList();

            return new PublicProfileDto(
                    "USER",
                    user.getUsername(),
                    city,
                    state,
                    skills,
                    postedRequests,
                    participationDtos,
                    score
            );
        }

        Optional<Organization> orgOpt =
                organizationRepository.findById(id);

        if(orgOpt.isPresent()){

            Organization org = orgOpt.get();
            OrganizationAddress organizationAddress = organizationAddressRepository.findByOrganizationId(org.getId());

            String city = (organizationAddress == null)? "-":organizationAddress.getCity();
            String state = (organizationAddress == null)? "-":organizationAddress.getState();
            Double score = organizationParticipationRepository.findScore(org.getId());
            List<ServiceRequest> requests =
                    serviceRequestRepository.findByOrganization_Id(org.getId());
            System.out.println("SErvice REquest "+ requests);
            List<PublicRequestDto> postedRequests =
                    requests.stream()
                            .map(r -> new PublicRequestDto(
                                    r.getId(),
                                    r.getTitle(),
                                    r.getCategory().name(),
                                    r.getStatus().name(),
                                    r.getState(),
                                    r.getCity()))
                            .toList();
            List<OrganizationParticipation> participations =
                    organizationParticipationRepository.findByOrganization_Id(org.getId());

            List<PublicParticipationDto> participationDtos =
                    participations.stream()
                            .map(p -> new PublicParticipationDto(
                                    p.getServiceRequest().getId(),
                                    p.getServiceRequest().getTitle(),
                                    p.getStatus().name()
                            ))
                            .toList();

            return new PublicProfileDto(
                    "ORGANIZATION",
                    org.getOrgName(),
                    city,
                    state,
                    List.of(),
                    postedRequests,
                    participationDtos,
                    score
            );
        }

        throw new RuntimeException("Profile not found");
    }
}
