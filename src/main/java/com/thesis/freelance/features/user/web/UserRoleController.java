package com.thesis.freelance.features.user.web;

import com.thesis.freelance.features.user.dto.UserRoleDto;
import com.thesis.freelance.features.user.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class UserRoleController {

    private final UserRoleService roleService;

    @GetMapping
    public List<UserRoleDto> getRoles() {
        return roleService.getAll();
    }
}