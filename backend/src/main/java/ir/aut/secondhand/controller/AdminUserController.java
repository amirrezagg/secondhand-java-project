package ir.aut.secondhand.controller;

import ir.aut.secondhand.dto.UserAdminResponse;
import ir.aut.secondhand.service.AdminUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> toggleUserBlock(@PathVariable Long userId) {
        String message = adminUserService.toggleUserBlockStatus(userId);
        return ResponseEntity.ok(message);
    }
}