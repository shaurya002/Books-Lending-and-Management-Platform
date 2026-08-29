package com.shaurya.booksmanagementplatform.service;

import com.shaurya.booksmanagementplatform.dto.request.UserRequest;
import com.shaurya.booksmanagementplatform.dto.response.UserResponse;

public interface UserService {
    UserResponse createUser(UserRequest request);
}
