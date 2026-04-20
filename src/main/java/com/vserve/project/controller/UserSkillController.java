package com.vserve.project.controller;

import com.vserve.project.entity.Skill;
import com.vserve.project.entity.UserSkill;
import com.vserve.project.enums.ProficiencyLevel;
import com.vserve.project.service.UserSkillService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
public class UserSkillController {

    private final UserSkillService userSkillService;

    public UserSkillController(UserSkillService userSkillService) {
        this.userSkillService = userSkillService;
    }

    @PostMapping("/names/add")
    public String addSkillNames(@RequestBody List<String> skillNames) {
        return userSkillService.addSkillNames(skillNames);
    }

    @PostMapping("/add")
    public String addSkills(
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "skillIds") List<Long> skillIds,
            @RequestParam(name = "proficiencyLevel") ProficiencyLevel proficiencyLevel) {
        return userSkillService.addSkills(userId, skillIds, proficiencyLevel
        );
    }

    @GetMapping("/user-id/{userId}")
    public List<UserSkill> getUserSkills(@PathVariable("userId") Long userId) {
        return userSkillService.getUserSkills(userId);
    }

    @DeleteMapping("/user-id/{userId}/skill-id/{skillId}")
    public String deleteSkill(@PathVariable("userId") Long userId,
                              @PathVariable("skillId") Long skillId) {
        return userSkillService.deleteSkill(userId, skillId);
    }

    @GetMapping("/search")
    public List<Skill> searchSkills(@RequestParam(name = "keyword") String keyword) {
        return userSkillService.searchSkills(keyword);
    }

    @PostMapping("/custom")
    public String createCustomSkill(@RequestParam(name = "skillName") String skillName) {
        return userSkillService.createCustomSkill(skillName);
    }

}
