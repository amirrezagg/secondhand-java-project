package ir.aut.secondhand.controller;

import ir.aut.secondhand.dto.UserAdminResponse;
import ir.aut.secondhand.service.AdminUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public ResponseEntity<Page<UserAdminResponse>> getAllUsers(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<UserAdminResponse> users = adminUserService.getAllUsersForAdmin(pageable);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/{userId}/toggle-block")
    public ResponseEntity<Map<String, Object>> toggleUserBlock(@PathVariable("userId") Long userId) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("isBlocked", adminUserService.toggleUserBlockStatus(userId));
        return ResponseEntity.ok(response);
    }
}