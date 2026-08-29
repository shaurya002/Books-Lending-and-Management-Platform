package com.shaurya.booksmanagementplatform.service.impl;

import com.shaurya.booksmanagementplatform.dto.request.MemberRequest;
import com.shaurya.booksmanagementplatform.dto.response.MemberResponse;
import com.shaurya.booksmanagementplatform.dto.response.PageResponse;
import com.shaurya.booksmanagementplatform.exception.DuplicateEmailException;
import com.shaurya.booksmanagementplatform.exception.InvalidMembershipDateRangeException;
import com.shaurya.booksmanagementplatform.exception.MemberNotFoundException;
import com.shaurya.booksmanagementplatform.mapper.MemberMapper;
import com.shaurya.booksmanagementplatform.model.entity.Member;
import com.shaurya.booksmanagementplatform.model.entity.User;
import com.shaurya.booksmanagementplatform.model.enums.MemberStatus;
import com.shaurya.booksmanagementplatform.model.enums.Role;
import com.shaurya.booksmanagementplatform.repositories.MemberRepository;
import com.shaurya.booksmanagementplatform.repositories.UserRepository;
import com.shaurya.booksmanagementplatform.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final int pageSize = 7;

    @Override
    @Transactional
    public MemberResponse createMember(MemberRequest request){
        if(memberRepository.existsByEmail(request.email())){
            throw new DuplicateEmailException(
                    "Member with email '" + request.email() + "' already exists."
            );
        }
        if (userRepository.existsByUsername(request.email())) {
            throw new DuplicateEmailException(
                    "User with username '" + request.email() + "' already exists."
            );
        }
        Member member = memberMapper.toEntity(request);

        member.setMembershipDate(LocalDate.now());
        member.setMemberStatus(MemberStatus.ACTIVE);

        Member savedMember = memberRepository.save(member);

        User user = User.builder()
                .username(savedMember.getEmail())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.MEMBER)
                .member(savedMember)
                .build();

        userRepository.save(user);
        return memberMapper.toResponse(savedMember);
    }

    @Override
    @Transactional
    public MemberResponse updateMember(Long id, MemberRequest request){
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Member by id " + id + " not found"));

        String oldEmail = member.getEmail();
        if (!oldEmail.equals(request.email())
                && memberRepository.existsByEmail(request.email())) {

            throw new DuplicateEmailException(
                    "Member with email '" + request.email() + "' already exists."
            );
        }

        member.setFirstName(request.firstName());
        member.setLastName(request.lastName());
        member.setEmail(request.email());
        member.setPhone(request.phone());

        if (!oldEmail.equals(request.email())) {
            User user = member.getUser();
            if (user != null) {
                user.setUsername(request.email());
            }
        }

        Member updatedMember = memberRepository.save(member);

        return memberMapper.toResponse(updatedMember);
    }


    @Override
    @Transactional
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Member by id " + id + " not found"));

        if (member.getUser() != null) {
            userRepository.delete(member.getUser());
        }
        memberRepository.delete(member);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponse getMemberById(Long id){
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Member by id " + id + " not found"));
        return memberMapper.toResponse(member);
    }
    @Override
    @Transactional(readOnly = true)
    public MemberResponse getMemberByEmail(String email){
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException("Member by email " + email + " not found"));
        return memberMapper.toResponse(member);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MemberResponse> getAllMembers(int page){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.ASC, "firstName"));
        Page<Member> memberPage = memberRepository.findAll(pageable);
        List<MemberResponse> members = memberPage.getContent().stream().map(memberMapper::toResponse).toList();
        return new PageResponse<>(
                members,
                memberPage.getNumber(),
                memberPage.getSize(),
                memberPage.getTotalElements(),
                memberPage.getTotalPages(),
                memberPage.isFirst(),
                memberPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MemberResponse> findByMemberStatus(MemberStatus memberStatus, int page){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.ASC, "firstName"));
        Page<Member> memberPage = memberRepository.findByMemberStatus(memberStatus, pageable);
        List<MemberResponse> members = memberPage.getContent().stream().map(memberMapper::toResponse).toList();
        return new PageResponse<>(
                members,
                memberPage.getNumber(),
                memberPage.getSize(),
                memberPage.getTotalElements(),
                memberPage.getTotalPages(),
                memberPage.isFirst(),
                memberPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MemberResponse> findByMembershipDateBetween(LocalDate startDate, LocalDate endDate, int page){
        LocalDate currentDate = LocalDate.now();
        if(startDate.isAfter(endDate)){
            throw new InvalidMembershipDateRangeException(
                    "Start date cannot be after end date"
            );
        }
        if(endDate.isAfter(currentDate)){
            throw new InvalidMembershipDateRangeException(
                    "End date cannot be in the future"
            );
        }

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.ASC, "firstName"));
        Page<Member> memberPage = memberRepository.findByMembershipDateBetween(startDate, endDate, pageable);
        List<MemberResponse> members = memberPage.getContent().stream().map(memberMapper::toResponse).toList();
        return new PageResponse<>(
                members,
                memberPage.getNumber(),
                memberPage.getSize(),
                memberPage.getTotalElements(),
                memberPage.getTotalPages(),
                memberPage.isFirst(),
                memberPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MemberResponse> findByName(String name, int page) {

        Pageable pageable =
                PageRequest.of(page, pageSize, Sort.by(Sort.Direction.ASC, "firstName"));

        Page<Member> memberPage =
                memberRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name, pageable);

        List<MemberResponse> members =
                memberPage.getContent()
                        .stream()
                        .map(memberMapper::toResponse)
                        .toList();

        return new PageResponse<>(
                members,
                memberPage.getNumber(),
                memberPage.getSize(),
                memberPage.getTotalElements(),
                memberPage.getTotalPages(),
                memberPage.isFirst(),
                memberPage.isLast()
        );
    }
}
