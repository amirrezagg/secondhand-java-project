package ir.aut.secondhand.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ir.aut.secondhand.dto.UserAdminResponse;
import ir.aut.secondhand.exception.ResourceNotFoundException;
import ir.aut.secondhand.model.User;
import ir.aut.secondhand.repository.UserRepository;

/**
 * Service responsible for administrative user management, including providing
 * paginated user data for the admin interface and changing the blocked state
 * of users while enforcing self-modification restrictions.
 */
@Service
public class AdminUserService {

    private final UserRepository userRepository;
    private final UserService userService;

    public AdminUserService(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * Returns a paginated collection of users converted into admin-facing response DTOs.
     * This is used to supply the admin dashboard with a page of users without exposing
     * the underlying entity structure directly.
     *
     * @param pageable paging information supplied by the controller
     * @return a page of UserAdminResponse instances
     */
    public Page<UserAdminResponse> getAllUsersForAdmin(Pageable pageable) {

        Page<User> userPage = userRepository.findAll(pageable);
        List<UserAdminResponse> dtoList = new ArrayList<>();

        for (User user : userPage.getContent()) {
            dtoList.add(new UserAdminResponse(user));
        }

        return new PageImpl<>(dtoList, pageable, userPage.getTotalElements());
    }

    /**
     * Toggles the blocked status of a user. Prevents administrators from blocking
     * or unblocking themselves. The operation is atomic and transactional.
     *
     * @param userId the ID of the user whose blocked status is to be toggled
     * @return true if the user is blocked after the operation; false otherwise
     * @throws IllegalArgumentException if attempting to toggle the current admin's own status
     * @throws ResourceNotFoundException if the specified user does not exist
     */
    @Transactional
    public boolean toggleUserBlockStatus(Long userId) {
        // Retrieve the currently authenticated admin user
        User admin = userService.getCurrentUser();

        // Prevent self-modification to avoid locking oneself out of admin functionality
        if (admin.getId().equals(userId)) {
            throw new IllegalArgumentException("You cannot block or unblock yourself");
        }

        // Fetch the target user; throw exception if not found
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user"));

        // Toggle the blocked state and persist the change
        user.setBlocked(!user.isBlocked());
        userRepository.save(user);

        return user.isBlocked();
    }
}