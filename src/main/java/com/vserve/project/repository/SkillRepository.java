package com.vserve.project.repository;

import com.vserve.project.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill,Long> {

    List<Skill> findBySkillNameContainingIgnoreCase(String name);

}
