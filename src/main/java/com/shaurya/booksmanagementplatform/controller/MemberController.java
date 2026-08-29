package com.shaurya.booksmanagementplatform.controller;

import com.shaurya.booksmanagementplatform.dto.request.MemberRequest;
import com.shaurya.booksmanagementplatform.dto.response.MemberResponse;
import com.shaurya.booksmanagementplatform.dto.response.PageResponse;
import com.shaurya.booksmanagementplatform.model.enums.MemberStatus;
import com.shaurya.booksmanagementplatform.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(
        name = "Members",
        description = "APIs for managing members"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @Operation(
            summary = "Create a new member",
            description = "Creates a new member entry"
    )
    @PostMapping
    public ResponseEntity<MemberResponse> createMember(@Valid @RequestBody MemberRequest memberRequest){
        MemberResponse memberResponse = memberService.createMember(memberRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(memberResponse);
    }

    @Operation(
            summary = "Get all members",
            description = "Retrieves a paginated list of all members"
    )
    @GetMapping
    public ResponseEntity<PageResponse<MemberResponse>> getAllMembers(@RequestParam(defaultValue = "0") int page){
        PageResponse<MemberResponse> memberResponsePage = memberService.getAllMembers(page);
        return ResponseEntity.status(HttpStatus.OK).body(memberResponsePage);
    }

    @Operation(
            summary = "Get member by ID",
            description = "Retrieves a member by their ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMemberById(@PathVariable Long id){
        MemberResponse member = memberService.getMemberById(id);
        return ResponseEntity.status(HttpStatus.OK).body(member);
    }

    @Operation(
            summary = "Get member by email",
            description = "Retrieves a member by their email"
    )
    @GetMapping("/email/{email}")
    public ResponseEntity<MemberResponse> getMemberByEmail(@PathVariable String email){
        MemberResponse member = memberService.getMemberByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(member);
    }

    @Operation(
            summary = "Get members by status",
            description = "Retrieves a list of members by their status"
    )
    @GetMapping("/status")
    public ResponseEntity<PageResponse<MemberResponse>> getMembersByStatus(@RequestParam MemberStatus memberStatus, @RequestParam(defaultValue = "0") int page){
        PageResponse<MemberResponse> members = memberService.findByMemberStatus(memberStatus, page);
        return ResponseEntity.status(HttpStatus.OK).body(members);
    }

    @Operation(
            summary = "Get members by membership date",
            description = "Retrieves a list of members by their membership date"
    )
    @GetMapping("/membership-date")
    public ResponseEntity<PageResponse<MemberResponse>> getMembersByMembershipDate(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(defaultValue = "0") int page) {

        PageResponse<MemberResponse> members =
                memberService.findByMembershipDateBetween(startDate, endDate, page);

        return ResponseEntity.ok(members);
    }

    @Operation(
            summary = "Update a member",
            description = "Updates the information of an existing member"
    )
    @PutMapping("/{id}")
    public ResponseEntity<MemberResponse> updateMember(@Valid @RequestBody MemberRequest memberRequest, @PathVariable Long id){
        MemberResponse memberResponse = memberService.updateMember(id, memberRequest);
        return ResponseEntity.status(HttpStatus.OK).body(memberResponse);
    }

    @Operation(
            summary = "Delete a member",
            description = "Deletes a member by their ID"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id){
        memberService.deleteMember(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary = "Search members by name",
            description = "Retrieves a list of members by their name"
    )
    @GetMapping("/search")
    public ResponseEntity<PageResponse<MemberResponse>> findByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page) {

        PageResponse<MemberResponse> response =
                memberService.findByName(name, page);

        return ResponseEntity.ok(response);
    }
}


