package com.shaurya.librarymanagementsystem.serviceTests;

import com.shaurya.librarymanagementsystem.dto.request.MemberRequest;
import com.shaurya.librarymanagementsystem.dto.response.MemberResponse;
import com.shaurya.librarymanagementsystem.dto.response.PageResponse;
import com.shaurya.librarymanagementsystem.exception.DuplicateEmailException;
import com.shaurya.librarymanagementsystem.exception.InvalidMembershipDateRangeException;
import com.shaurya.librarymanagementsystem.exception.MemberNotFoundException;
import com.shaurya.librarymanagementsystem.mapper.MemberMapper;
import com.shaurya.librarymanagementsystem.model.entity.Member;
import com.shaurya.librarymanagementsystem.model.entity.User;
import com.shaurya.librarymanagementsystem.model.enums.MemberStatus;
import com.shaurya.librarymanagementsystem.repositories.MemberRepository;
import com.shaurya.librarymanagementsystem.repositories.UserRepository;
import com.shaurya.librarymanagementsystem.service.impl.MemberServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberMapper memberMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberServiceImpl memberService;

    @Test
    void createMember_shouldSaveAndReturnResponse_whenRequestIsValid() {
        MemberRequest req = new MemberRequest("John", "Doe", "john@example.com", "0123456789", "pass");
        Member mapped = Member.builder().firstName(req.firstName()).lastName(req.lastName()).email(req.email()).phone(req.phone()).build();
        Member saved = Member.builder().id(11L).firstName(req.firstName()).lastName(req.lastName()).email(req.email()).phone(req.phone()).membershipDate(LocalDate.now()).memberStatus(MemberStatus.ACTIVE).build();
        MemberResponse expected = new MemberResponse(11L, req.firstName(), req.lastName(), req.email(), req.phone(), saved.getMembershipDate(), MemberStatus.ACTIVE);

        when(memberRepository.existsByEmail(req.email())).thenReturn(false);
        when(userRepository.existsByUsername(req.email())).thenReturn(false);
        when(memberMapper.toEntity(req)).thenReturn(mapped);
        when(memberRepository.save(mapped)).thenReturn(saved);
        when(passwordEncoder.encode(req.password())).thenReturn("encoded");
        when(memberMapper.toResponse(saved)).thenReturn(expected);

        MemberResponse actual = memberService.createMember(req);

        assertEquals(expected, actual);
        verify(memberRepository).existsByEmail(req.email());
        verify(userRepository).existsByUsername(req.email());
        verify(memberRepository).save(mapped);
        verify(userRepository).save(any(User.class));
        verify(memberMapper).toResponse(saved);
    }

    @Test
    void createMember_shouldThrowDuplicateEmail_whenMemberEmailExists() {
        MemberRequest req = new MemberRequest("J","D","a@b.com","0123456789","p");
        when(memberRepository.existsByEmail(req.email())).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> memberService.createMember(req));

        verify(memberRepository).existsByEmail(req.email());
        verifyNoMoreInteractions(memberRepository, userRepository, memberMapper);
    }

    @Test
    void createMember_shouldThrowDuplicateEmail_whenUsernameExists() {
        MemberRequest req = new MemberRequest("J","D","u@b.com","0123456789","p");
        when(memberRepository.existsByEmail(req.email())).thenReturn(false);
        when(userRepository.existsByUsername(req.email())).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> memberService.createMember(req));

        verify(memberRepository).existsByEmail(req.email());
        verify(userRepository).existsByUsername(req.email());
        verifyNoMoreInteractions(memberRepository, userRepository, memberMapper);
    }

    @Test
    void updateMember_shouldUpdateAndReturnResponse_whenValid() {
        Long id = 2L;
        MemberRequest req = new MemberRequest("A","B","a@b.com","0123456789","pwd");
        Member existing = Member.builder().id(id).firstName("Old").lastName("OldLast").email("old@b.com").phone("0000000000").build();
        Member updated = Member.builder().id(id).firstName(req.firstName()).lastName(req.lastName()).email(req.email()).phone(req.phone()).build();
        MemberResponse expected = new MemberResponse(id, req.firstName(), req.lastName(), req.email(), req.phone(), null, null);

        when(memberRepository.findById(id)).thenReturn(Optional.of(existing));
        when(memberRepository.existsByEmail(req.email())).thenReturn(false);
        when(memberRepository.save(existing)).thenReturn(updated);
        when(memberMapper.toResponse(updated)).thenReturn(expected);

        MemberResponse actual = memberService.updateMember(id, req);

        assertEquals(expected, actual);
        verify(memberRepository).findById(id);
        verify(memberRepository).save(existing);
        verify(memberMapper).toResponse(updated);
    }

    @Test
    void updateMember_shouldThrowNotFound_whenMissing() {
        Long id = 99L;
        when(memberRepository.findById(id)).thenReturn(Optional.empty());
        MemberRequest req = new MemberRequest("x","y","e@e.com","0123456789","p");

        assertThrows(MemberNotFoundException.class, () -> memberService.updateMember(id, req));
        verify(memberRepository).findById(id);
    }

    @Test
    void updateMember_shouldThrowDuplicateEmail_whenEmailTaken() {
        Long id = 3L;
        Member existing = Member.builder().id(id).email("old@e.com").build();
        MemberRequest req = new MemberRequest("f","l","taken@e.com","0123456789","p");

        when(memberRepository.findById(id)).thenReturn(Optional.of(existing));
        when(memberRepository.existsByEmail(req.email())).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> memberService.updateMember(id, req));

        verify(memberRepository).findById(id);
        verify(memberRepository).existsByEmail(req.email());
    }

    @Test
    void deleteMember_shouldDeleteAndRemoveUser_whenPresent() {
        Long id = 4L;
        User user = User.builder().id(1L).username("u").build();
        Member m = Member.builder().id(id).email("e").user(user).build();
        when(memberRepository.findById(id)).thenReturn(Optional.of(m));

        memberService.deleteMember(id);

        verify(memberRepository).findById(id);
        verify(userRepository).delete(user);
        verify(memberRepository).delete(m);
    }

    @Test
    void deleteMember_shouldThrowNotFound_whenMissing() {
        Long id = 120L;
        when(memberRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.deleteMember(id));
        verify(memberRepository).findById(id);
    }

    @Test
    void getMemberById_shouldReturnResponse_whenFound() {
        Long id = 5L;
        Member m = Member.builder().id(id).firstName("F").lastName("L").email("e").phone("0123456789").membershipDate(LocalDate.now()).memberStatus(MemberStatus.ACTIVE).build();
        MemberResponse expected = new MemberResponse(id, "F", "L", "e", "0123456789", m.getMembershipDate(), MemberStatus.ACTIVE);

        when(memberRepository.findById(id)).thenReturn(Optional.of(m));
        when(memberMapper.toResponse(m)).thenReturn(expected);

        MemberResponse actual = memberService.getMemberById(id);
        assertEquals(expected, actual);
        verify(memberRepository).findById(id);
        verify(memberMapper).toResponse(m);
    }

    @Test
    void getMemberById_shouldThrowNotFound_whenMissing() {
        Long id = 200L;
        when(memberRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.getMemberById(id));
        verify(memberRepository).findById(id);
    }

    @Test
    void getMemberByEmail_shouldReturnResponse_whenFound() {
        String email = "a@b.com";
        Member m = Member.builder().id(6L).email(email).build();
        MemberResponse expected = new MemberResponse(6L, null, null, email, null, null, null);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(m));
        when(memberMapper.toResponse(m)).thenReturn(expected);

        MemberResponse actual = memberService.getMemberByEmail(email);
        assertEquals(expected, actual);
        verify(memberRepository).findByEmail(email);
        verify(memberMapper).toResponse(m);
    }

    @Test
    void getMemberByEmail_shouldThrowNotFound_whenMissing() {
        String email = "no@e.com";
        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.getMemberByEmail(email));
        verify(memberRepository).findByEmail(email);
    }

    @Test
    void getAllMembers_shouldReturnPage() {
        Member m = Member.builder().id(1L).firstName("A").build();
        Page<Member> page = new PageImpl<>(List.of(m));
        MemberResponse dto = new MemberResponse(1L, "A", null, null, null, null, null);

        when(memberRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(memberMapper.toResponse(m)).thenReturn(dto);

        PageResponse<MemberResponse> resp = memberService.getAllMembers(0);
        assertEquals(1, resp.content().size());
        assertEquals(dto, resp.content().get(0));
    }

    @Test
    void findByMemberStatus_shouldReturnMatches() {
        Member m = Member.builder().id(2L).firstName("B").memberStatus(MemberStatus.ACTIVE).build();
        Page<Member> page = new PageImpl<>(List.of(m));
        MemberResponse dto = new MemberResponse(2L, "B", null, null, null, null, MemberStatus.ACTIVE);

        when(memberRepository.findByMemberStatus(eq(MemberStatus.ACTIVE), any(Pageable.class))).thenReturn(page);
        when(memberMapper.toResponse(m)).thenReturn(dto);

        PageResponse<MemberResponse> resp = memberService.findByMemberStatus(MemberStatus.ACTIVE, 0);
        assertEquals(1, resp.content().size());
        assertEquals(dto, resp.content().get(0));
    }

    @Test
    void findByMembershipDateBetween_shouldReturnMatches_whenValidRange() {
        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now().minusDays(1);
        Member m = Member.builder().id(3L).firstName("C").membershipDate(start.plusDays(1)).build();
        Page<Member> page = new PageImpl<>(List.of(m));
        MemberResponse dto = new MemberResponse(3L, "C", null, null, null, m.getMembershipDate(), null);

        when(memberRepository.findByMembershipDateBetween(eq(start), eq(end), any(Pageable.class))).thenReturn(page);
        when(memberMapper.toResponse(m)).thenReturn(dto);

        PageResponse<MemberResponse> resp = memberService.findByMembershipDateBetween(start, end, 0);
        assertEquals(1, resp.content().size());
        assertEquals(dto, resp.content().get(0));
    }

    @Test
    void findByMembershipDateBetween_shouldThrowInvalidRange_whenStartAfterEnd() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().minusDays(5);

        assertThrows(InvalidMembershipDateRangeException.class, () -> memberService.findByMembershipDateBetween(start, end, 0));
    }

    @Test
    void findByMembershipDateBetween_shouldThrowInvalidRange_whenEndInFuture() {
        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now().plusDays(5);

        assertThrows(InvalidMembershipDateRangeException.class, () -> memberService.findByMembershipDateBetween(start, end, 0));
    }

    @Test
    void findByName_shouldReturnMatches() {
        Member m = Member.builder().id(7L).firstName("Alice").lastName("Smith").build();
        Page<Member> page = new PageImpl<>(List.of(m));
        MemberResponse dto = new MemberResponse(7L, "Alice", "Smith", null, null, null, null);

        when(memberRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(eq("Ali"), eq("Ali"), any(Pageable.class))).thenReturn(page);
        when(memberMapper.toResponse(m)).thenReturn(dto);

        PageResponse<MemberResponse> resp = memberService.findByName("Ali", 0);
        assertEquals(1, resp.content().size());
        assertEquals(dto, resp.content().get(0));
    }
}
