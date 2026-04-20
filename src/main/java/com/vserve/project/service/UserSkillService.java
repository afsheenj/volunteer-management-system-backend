package com.vserve.project.service;

import com.vserve.project.entity.Skill;
import com.vserve.project.entity.UserSkill;
import com.vserve.project.enums.ProficiencyLevel;

import java.util.List;

public interface UserSkillService {

    String addSkills(Long userId, List<Long> skillIds, ProficiencyLevel proficiencyLevel);

    List<UserSkill> getUserSkills(Long userId);

    String deleteSkill(Long userId, Long skillId);

    List<Skill> searchSkills(String keyword);

    String addSkillNames(List<String> skillNames);

    String createCustomSkill(String skillName);

}
