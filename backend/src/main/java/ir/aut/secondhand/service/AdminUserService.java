package ir.aut.secondhand.service;

import ir.aut.secondhand.dto.UserAdminResponse;
import ir.aut.secondhand.exception.ResourceNotFoundException;
import ir.aut.secondhand.model.User;
import ir.aut.secondhand.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminUserService {

    private final UserRepository userRepository;
    private final UserService userService;

    public AdminUserService(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public Page<UserAdminResponse> getAllUsersForAdmin(Pageable pageable) {

        Page<User> userPage = userRepository.findAll(pageable);
        List<UserAdminResponse> dtoList = new ArrayList<>();

        for (User user : userPage.getContent()) {
            dtoList.add(new UserAdminResponse(user));
        }

        return new PageImpl<>(dtoList, pageable, userPage.getTotalElements());
    }

    @Transactional
    public boolean toggleUserBlockStatus(Long userId) {
        User admin = userService.getCurrentUser();

        if (admin.getId().equals(userId)) {
            throw new IllegalArgumentException("You cannot block or unblock yourself");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user"));

        user.setBlocked(!user.isBlocked());
        userRepository.save(user);

        return user.isBlocked();
    }
}