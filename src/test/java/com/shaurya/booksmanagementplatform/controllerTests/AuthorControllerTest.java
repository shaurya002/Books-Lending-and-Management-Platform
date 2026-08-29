package com.shaurya.booksmanagementplatform.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaurya.booksmanagementplatform.controller.AuthorController;
import com.shaurya.booksmanagementplatform.dto.request.AuthorRequest;
import com.shaurya.booksmanagementplatform.dto.response.AuthorResponse;
import com.shaurya.booksmanagementplatform.dto.response.PageResponse;
import com.shaurya.booksmanagementplatform.service.AuthorService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AuthorControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private AuthorService authorService;

    @InjectMocks
    private AuthorController authorController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(authorController).build();
    }

    @Test
    void createAuthor_returnsCreated() throws Exception {
        AuthorRequest req = new AuthorRequest("Jane Doe", "Bio");
        AuthorResponse resp = new AuthorResponse(7L, "Jane Doe", "Bio");

        Mockito.when(authorService.createAuthor(any(AuthorRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(7));
    }

    @Test
    void getAuthorById_returnsOk() throws Exception {
        AuthorResponse resp = new AuthorResponse(8L, "Auth", "B");
        Mockito.when(authorService.getAuthorById(8L)).thenReturn(resp);

        mockMvc.perform(get("/authors/8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(8))
                .andExpect(jsonPath("$.name").value("Auth"));
    }

    @Test
    void deleteAuthor_returnsNoContent() throws Exception {
        Mockito.doNothing().when(authorService).deleteAuthor(9L);

        mockMvc.perform(delete("/authors/9"))
                .andExpect(status().isNoContent());
    }

    @Test
    void findByName_returnsPage() throws Exception {
        AuthorResponse a = new AuthorResponse(10L, "SearchName", "bio");
        PageResponse<AuthorResponse> page = new PageResponse<>(List.of(a), 0, 1, 1L, 1, true, true);
        Mockito.when(authorService.findByName(eq("Search"), eq(0))).thenReturn(page);

        mockMvc.perform(get("/authors/name").param("name", "Search").param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(10));
    }
}
