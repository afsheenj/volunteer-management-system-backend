package com.vserve.project.controller;

import com.vserve.project.enums.AccountStatus;
import com.vserve.project.enums.Category;
import com.vserve.project.enums.Role;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/enums")
public class EnumController {

//    @GetMapping("/categories")
//    public Category[] getCategories() {
//        return Category.values();
//    }

    @GetMapping("/categories")
    public List<String> getCategories() {
        return Arrays.stream(Category.values())
                .map(Enum::name)
                .toList();
    }

    @GetMapping("/roles")
    public Role[] getRoles() {
        return Role.values();
    }

    @GetMapping("/account-status")
    public AccountStatus[] getAccountStatus() {
        return AccountStatus.values();
    }

}
