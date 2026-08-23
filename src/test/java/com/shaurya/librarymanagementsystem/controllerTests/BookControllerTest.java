package com.shaurya.librarymanagementsystem.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaurya.librarymanagementsystem.controller.BookController;
import com.shaurya.librarymanagementsystem.dto.request.BookRequest;
import com.shaurya.librarymanagementsystem.dto.response.BookResponse;
import com.shaurya.librarymanagementsystem.model.enums.BookStatus;
import com.shaurya.librarymanagementsystem.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    void createBook_returnsCreated() throws Exception {
        BookRequest req = new BookRequest("Title","isbn","Genre",2020,2,List.of(1L));
        BookResponse resp = new BookResponse(1L,"Title","isbn","Genre",2020,2,2, BookStatus.AVAILABLE, List.of("Author"));

        Mockito.when(bookService.createBook(any(BookRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getBookById_returnsOk() throws Exception {
        BookResponse resp = new BookResponse(2L,"T","isbn","G",2019,1,1, BookStatus.AVAILABLE, List.of());
        Mockito.when(bookService.getBookById(2L)).thenReturn(resp);

        mockMvc.perform(get("/books/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.title").value("T"));
    }
}
