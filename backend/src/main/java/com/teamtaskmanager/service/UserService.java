package com.teamtaskmanager.service;

import com.teamtaskmanager.dto.UserSummaryResponse;
import com.teamtaskmanager.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final CurrentUserService currentUserService;

    public UserService(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public UserSummaryResponse getMe() {
        User user = currentUserService.getCurrentUser();
        return new UserSummaryResponse(user.getId(), user.getName(), user.getEmail());
    }
}
