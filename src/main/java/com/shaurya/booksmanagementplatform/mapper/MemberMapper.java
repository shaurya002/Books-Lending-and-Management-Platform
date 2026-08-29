package com.shaurya.booksmanagementplatform.mapper;

import com.shaurya.booksmanagementplatform.dto.request.MemberRequest;
import com.shaurya.booksmanagementplatform.dto.response.MemberResponse;
import com.shaurya.booksmanagementplatform.model.entity.Member;
import org.springframework.stereotype.Component;


@Component
public class MemberMapper {

    public Member toEntity(MemberRequest memberRequest) {
        if (memberRequest == null) {
            return null;
        }
        return Member.builder()
                .firstName(memberRequest.firstName())
                .lastName(memberRequest.lastName())
                .email(memberRequest.email())
                .phone(memberRequest.phone())
                .build();
    }

    public MemberResponse toResponse(Member member) {
        if (member == null) {
            return null;
        }
        return new MemberResponse(
                member.getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail(),
                member.getPhone(),
                member.getMembershipDate(),
                member.getMemberStatus()
        );
    }
}
