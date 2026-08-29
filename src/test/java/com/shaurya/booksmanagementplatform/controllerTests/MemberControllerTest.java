package com.shaurya.booksmanagementplatform.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaurya.booksmanagementplatform.controller.MemberController;
import com.shaurya.booksmanagementplatform.dto.request.MemberRequest;
import com.shaurya.booksmanagementplatform.dto.response.MemberResponse;
import com.shaurya.booksmanagementplatform.model.enums.MemberStatus;
import com.shaurya.booksmanagementplatform.service.MemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MemberControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
    }

    @Test
    void createMember_returnsCreated() throws Exception {
        MemberRequest req = new MemberRequest("John","Doe","john@ex.com","0123456789","pass");
        MemberResponse resp = new MemberResponse(5L, "John","Doe","john@ex.com","0123456789", LocalDate.now(), MemberStatus.ACTIVE);

        Mockito.when(memberService.createMember(any(MemberRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void getMemberById_returnsOk() throws Exception {
        MemberResponse resp = new MemberResponse(6L, "A","B","a@b.com","0123456789", LocalDate.now(), MemberStatus.ACTIVE);
        Mockito.when(memberService.getMemberById(6L)).thenReturn(resp);

        mockMvc.perform(get("/members/6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(6))
                .andExpect(jsonPath("$.firstName").value("A"));
    }
}
