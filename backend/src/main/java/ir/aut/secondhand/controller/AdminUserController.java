package ir.aut.secondhand.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ir.aut.secondhand.dto.UserAdminResponse;
import ir.aut.secondhand.service.AdminUserService;

/**
 * REST controller that exposes administrative user management endpoints.
 *
 * <p>Provides paginated retrieval of users for administrative review and an
 * operation to toggle a user's blocked status. This controller delegates
 * business logic to AdminUserService and shapes HTTP responses for the
 * administration UI or other admin clients.</p>
 */
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    /**
     * Retrieve a paginated list of users tailored for administrative consumption.
     *
     * <p>Uses a default page size to protect the admin UI from large payloads and
     * delegates to the AdminUserService to assemble UserAdminResponse DTOs that
     * contain the data needed for moderation and management actions.</p>
     *
     * @param pageable pagination information (page number, size, sort)
     * @return HTTP 200 with a page of UserAdminResponse DTOs
     */
    @GetMapping
    public ResponseEntity<Page<UserAdminResponse>> getAllUsers(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<UserAdminResponse> users = adminUserService.getAllUsersForAdmin(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Toggle the blocked status of a user identified by userId.
     *
     * <p>This endpoint flips the user's current block flag (blocked -> unblocked,
     * or unblocked -> blocked) and returns a minimal JSON payload indicating the
     * resulting state. The operation is performed in the service layer which handles
     * persistence and any related business rules (audit, notifications, etc.).</p>
     *
     * @param userId the identifier of the user whose block status will be toggled
     * @return HTTP 200 with a map containing the resulting "isBlocked" boolean
     */
    @PostMapping("/{userId}/toggle-block")
    public ResponseEntity<Map<String, Object>> toggleUserBlock(@PathVariable("userId") Long userId) {
        // Prepare a simple ordered response payload to return the resulting state
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("isBlocked", adminUserService.toggleUserBlockStatus(userId));
        return ResponseEntity.ok(response);
    }
}