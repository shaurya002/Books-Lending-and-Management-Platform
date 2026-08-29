package com.shaurya.booksmanagementplatform.service.impl;

import com.shaurya.booksmanagementplatform.dto.request.UserRequest;
import com.shaurya.booksmanagementplatform.dto.response.UserResponse;
import com.shaurya.booksmanagementplatform.exception.MemberNotFoundException;
import com.shaurya.booksmanagementplatform.model.entity.Member;
import com.shaurya.booksmanagementplatform.model.entity.User;
import com.shaurya.booksmanagementplatform.model.enums.Role;
import com.shaurya.booksmanagementplatform.repositories.MemberRepository;
import com.shaurya.booksmanagementplatform.repositories.UserRepository;
import com.shaurya.booksmanagementplatform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createUser(UserRequest request){
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("User with username '" + request.username() + "' already exists.");
        }

        Member member = null;

        if(request.role()== Role.MEMBER) {
            if (request.memberId() == null) {
                throw new IllegalArgumentException("Member ID is required for MEMBER role.");
            }
            member = memberRepository.findById(request.memberId())
                    .orElseThrow(() -> new MemberNotFoundException("Member not found with ID: " + request.memberId()));

            if (!member.getEmail().equals(request.username())) {
                throw new IllegalArgumentException("Username for MEMBER must match the member's email.");
            }

            if (member.getUser() != null) {
                throw new IllegalArgumentException("Member with ID " + request.memberId() + " already has an associated user.");
            }
        } else if (request.role() == Role.LIBRARIAN) {
            if (request.memberId() != null) {
                throw new IllegalArgumentException("Member ID should not be provided for LIBRARIAN role.");
            }
        }

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .member(member)
                .build();
        User savedUser = userRepository.save(user);
        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getRole(),
                savedUser.getMember() != null ? savedUser.getMember().getId() : null
        );
    }
}
