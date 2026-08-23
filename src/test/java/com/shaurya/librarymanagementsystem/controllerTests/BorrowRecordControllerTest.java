package com.shaurya.librarymanagementsystem.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaurya.librarymanagementsystem.controller.BorrowRecordController;
import com.shaurya.librarymanagementsystem.dto.request.BorrowRecordRequest;
import com.shaurya.librarymanagementsystem.dto.response.BorrowRecordResponse;
import com.shaurya.librarymanagementsystem.model.enums.BorrowStatus;
import com.shaurya.librarymanagementsystem.service.BorrowRecordService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BorrowRecordControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private BorrowRecordService borrowRecordService;

    @InjectMocks
    private BorrowRecordController borrowRecordController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(borrowRecordController).build();
    }

    @Test
    void createBorrowRecord_returnsCreated() throws Exception {
        BorrowRecordRequest req = new BorrowRecordRequest(1L,2L);
        BorrowRecordResponse resp = new BorrowRecordResponse(10L,1L,"Title",2L,"M N", LocalDate.now(), LocalDate.now().plusDays(15), null, 0.0, BorrowStatus.BORROWED);

        Mockito.when(borrowRecordService.borrowBook(any(BorrowRecordRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/borrow-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void returnBorrowRecord_returnsOk() throws Exception {
        BorrowRecordResponse resp = new BorrowRecordResponse(11L,1L,"T",2L,"M N", LocalDate.now().minusDays(20), LocalDate.now().minusDays(5), LocalDate.now(), 5.0, BorrowStatus.RETURNED);
        Mockito.when(borrowRecordService.returnBook(11L)).thenReturn(resp);

        mockMvc.perform(put("/borrow-records/11/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(11));
    }

    @Test
    void getBorrowRecord_returnsOk() throws Exception {
        BorrowRecordResponse resp = new BorrowRecordResponse(12L,1L,"T",2L,"M N", null, null, null, 0.0, null);
        Mockito.when(borrowRecordService.getBorrowRecordById(12L)).thenReturn(resp);

        mockMvc.perform(get("/borrow-records/12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(12));
    }

    @Test
    void getAllBorrowRecords_returnsOk() throws Exception {
        BorrowRecordResponse resp = new BorrowRecordResponse(13L,1L,"T",2L,"M N", null, null, null, 0.0, null);
        Mockito.when(borrowRecordService.getAllBorrowRecords()).thenReturn(List.of(resp));

        mockMvc.perform(get("/borrow-records"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(13));
    }
}
