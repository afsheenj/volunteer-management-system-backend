package com.vserve.project.service.impl;

import com.vserve.project.dto.user.*;
import com.vserve.project.entity.User;
import com.vserve.project.entity.UserAddress;
import com.vserve.project.entity.UserParticipation;
import com.vserve.project.entity.UserSkill;
import com.vserve.project.exception.ResourceNotFoundException;
import com.vserve.project.repository.UserAddressRepository;
import com.vserve.project.repository.UserParticipationRepository;
import com.vserve.project.repository.UserRepository;
import com.vserve.project.repository.UserSkillRepository;
import com.vserve.project.service.UserProfileService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserProfileServiceImpl implements UserProfileService {
    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final UserSkillRepository userSkillRepository;
    private final UserParticipationRepository userParticipationRepository;

    public UserProfileServiceImpl(UserRepository userRepository, UserAddressRepository userAddressRepository, UserSkillRepository userSkillRepository, UserParticipationRepository userParticipationRepository) {
        this.userRepository = userRepository;
        this.userAddressRepository = userAddressRepository;
        this.userSkillRepository = userSkillRepository;
        this.userParticipationRepository = userParticipationRepository;
    }

    @Override
    public UserProfileResponseDto getUserProfile(Long id) {

        User user=userRepository.findById(id).orElse(null);
        if(user==null){
            throw new ResourceAccessException("User not found");
        }

       Double score = userParticipationRepository.findScore(id);

        return new UserProfileResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getAvailability(),
                user.getGender(),
                user.getDateOfBirth(),
                user.getBio(),
                score
        );
    }

    @Override
    public String updateUserProfile(Long id, UserProfileUpdateDto dto){
        User user=userRepository.findById(id).orElse(null);
        if(user==null){
            throw new ResourceAccessException("User not found");
        }
        if (dto.username() != null) {
            user.setUsername(dto.username());
        }

        if (dto.phone() != null) {
            user.setPhone(dto.phone());
        }

        if (dto.gender() != null) {
            user.setGender(dto.gender());
        }

        if (dto.availability() != null) {
            user.setAvailability(dto.availability());
        }

        if (dto.dateOfBirth() != null) {
            user.setDateOfBirth(dto.dateOfBirth());
        }

        if (dto.bio() != null) {
            user.setBio(dto.bio());
        }

        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        return "User profile updated successfully";
    }

    @Override
    public UserAddressDto getUserAddress(Long userId){
        UserAddress address = userAddressRepository.findByUserId(userId);

        if (address == null) {
            throw new ResourceNotFoundException("Address not found for this user");
        }

        return new UserAddressDto(
                address.getUser().getId(),
                address.getDoorNo(),
                address.getStreet(),
                address.getCity(),
                address.getDistrict(),
                address.getState(),
                address.getPincode()
        );
    }

    @Override
    public List<UserSkillResponseDto> getUserSkills(Long userId) {

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new ResourceAccessException("User not found");
        }

        List<UserSkill> userSkills = userSkillRepository.findByUser(user);

        return userSkills.stream()
                .map(us -> new UserSkillResponseDto(
                        us.getSkill().getSkillId(),
                        us.getSkill().getSkillName(),
                        us.getProficiencyLevel()
                ))
                .toList();
    }

        @Override
        public UserPublicProfileDto getPublicProfile(Long userId) {

            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                throw new ResourceAccessException("User not found");
            }


            UserAddress address = userAddressRepository.findByUserId(userId);


            List<UserSkill> userSkills = userSkillRepository.findByUser(user);

            List<UserSkillResponseDto> skillDtos = userSkillRepository.findByUser(user)
                    .stream()
                    .map(skill -> new UserSkillResponseDto(
                            skill.getId(),
                            skill.getSkill().getSkillName(),
                            skill.getProficiencyLevel()
                    ))
                    .toList();

            return new UserPublicProfileDto(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getRole().name(),
                    address != null ? address.getCity() : null,
                    address != null ? address.getState() : null,
                    skillDtos
            );
        }


}
