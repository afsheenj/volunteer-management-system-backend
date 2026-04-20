package com.vserve.project.service.impl;

import com.vserve.project.entity.Skill;
import com.vserve.project.entity.User;
import com.vserve.project.entity.UserSkill;
import com.vserve.project.enums.ProficiencyLevel;
import com.vserve.project.exception.BusinessException;
import com.vserve.project.repository.SkillRepository;
import com.vserve.project.repository.UserRepository;
import com.vserve.project.repository.UserSkillRepository;
import com.vserve.project.service.UserSkillService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

@Service
public class UserSkillServiceImpl implements UserSkillService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;


    public UserSkillServiceImpl(UserRepository userRepository, SkillRepository skillRepository, UserSkillRepository userSkillRepository) {
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.userSkillRepository = userSkillRepository;
    }

    @Override
    public String addSkillNames(List<String> skillNames) {

        if (skillNames == null || skillNames.isEmpty()) {
            throw new BusinessException("Skill list cannot be empty");
        }
        for (String name : skillNames) {
            Skill skill = new Skill();
            skill.setSkillName(name);
            skillRepository.save(skill);
        }

        return "Skills added successfully";
    }

    @Override
    public String addSkills(Long userId, List<Long> skillIds, ProficiencyLevel proficiencyLevel) {

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new BusinessException("User not found");
        }

        List<Skill> skills = skillRepository.findAllById(skillIds);

        if (skills.isEmpty()) {
            throw new BusinessException("Skills not found");
        }

        for (Skill skill : skills) {
            boolean exists = userSkillRepository.existsByUserAndSkill(user, skill);

            if (!exists) {
                UserSkill userSkill = mapToUserSkill(user, skill, proficiencyLevel);
                userSkillRepository.save(userSkill);
            }
        }
        return "Skills added successfully";
    }

    @Override
    public List<UserSkill> getUserSkills(Long userId) {

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new BusinessException("User not found");
        }
        return userSkillRepository.findByUser(user);
    }

    @Override
    public String deleteSkill(Long userId, Long skillId) {

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new BusinessException("User not found");
        }
        Skill skill = skillRepository.findById(skillId).orElse(null);
        if (skill == null) {
            throw new BusinessException("Skill not found");
        }

        UserSkill userSkill = userSkillRepository.findByUserAndSkill(user, skill).orElse(null);

        if (userSkill == null) {
            throw new BusinessException("UserSkill not found");
        }
        userSkillRepository.delete(userSkill);

        return "Skill deleted successfully";
    }

    @Override
    public List<Skill> searchSkills(String keyword) {

        if (keyword == null || keyword.trim().isEmpty()) {
            throw new BusinessException("Keyword cannot be empty");
        }

        List<Skill> skills = skillRepository.findBySkillNameContainingIgnoreCase(keyword);
        return skills;
    }

    @Override
    public String createCustomSkill(String skillName) {

        if (skillName == null || skillName.trim().isEmpty()) {
            throw new BusinessException("Skill name cannot be empty");
        }

        List<Skill> existing = skillRepository.findBySkillNameContainingIgnoreCase(skillName);

        if (!existing.isEmpty()) {
            return "Skill already exists";
        }
        Skill skill = new Skill();
        skill.setSkillName(skillName);
        skillRepository.save(skill);

        return "Custom skill added successfully";
    }

    private UserSkill mapToUserSkill(User user, Skill skill, ProficiencyLevel level) {

        UserSkill userSkill = new UserSkill();
        userSkill.setUser(user);
        userSkill.setSkill(skill);
        userSkill.setProficiencyLevel(level);

        return userSkill;
    }
}
