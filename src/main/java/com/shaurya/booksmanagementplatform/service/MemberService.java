package com.shaurya.booksmanagementplatform.service;

import com.shaurya.booksmanagementplatform.dto.request.MemberRequest;
import com.shaurya.booksmanagementplatform.dto.response.MemberResponse;
import com.shaurya.booksmanagementplatform.dto.response.PageResponse;
import com.shaurya.booksmanagementplatform.model.enums.MemberStatus;

import java.time.LocalDate;

public interface MemberService {

    MemberResponse createMember(MemberRequest request);
    MemberResponse updateMember(Long id, MemberRequest request);
    void deleteMember(Long id);
    MemberResponse getMemberById(Long id);
    MemberResponse getMemberByEmail(String email);
    PageResponse<MemberResponse> getAllMembers(int page);
    PageResponse<MemberResponse> findByMemberStatus(MemberStatus memberStatus, int page);
    PageResponse<MemberResponse> findByMembershipDateBetween(
            LocalDate startDate,
            LocalDate endDate,
            int page
    );
    PageResponse<MemberResponse> findByName(String name, int page);
}
