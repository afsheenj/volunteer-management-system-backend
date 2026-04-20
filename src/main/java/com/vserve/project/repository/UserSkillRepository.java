package com.vserve.project.repository;

import com.vserve.project.entity.Skill;
import com.vserve.project.entity.User;
import com.vserve.project.entity.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSkillRepository extends JpaRepository<UserSkill,Long> {

    List<UserSkill> findByUser(User user);

    boolean existsByUserAndSkill(User user, Skill skill);

//    Optional<UserSkill> findByIdAndUser(Long id, User user);

    Optional<UserSkill> findByUserAndSkill(User user, Skill skill);

}
