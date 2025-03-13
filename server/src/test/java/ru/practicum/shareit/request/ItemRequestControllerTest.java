package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.interfaces.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {
    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private NewItemRequestDto newItemRequestDto;

    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .build();

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("description");
        itemRequestDto.setCreated(null);
        itemRequestDto.setItems(List.of());

        newItemRequestDto = new NewItemRequestDto();
        newItemRequestDto.setDescription("description1");
    }

    @Test
    @DisplayName("ItemRequestController_getAllItemRequestsByUser")
    void getAllItemRequestsByUser_whenInvoked_thenHasCorrectResponse() throws Exception {
        when(itemRequestService.getAllItemRequestsByUser(1L)).thenReturn(List.of(itemRequestDto));
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestDto))));
    }

    @Test
    @DisplayName("ItemRequestController_getAllItemRequests")
    void getAllItemRequests_whenInvoked_thenHasCorrectResponse() throws Exception {
        when(itemRequestService.getAllItemRequests(1L)).thenReturn(List.of(itemRequestDto));
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestDto))));
    }

    @Test
    @DisplayName("ItemRequestController_getItemRequest")
    void getItemRequest_whenInvoked_thenHasCorrectResponse() throws Exception {
        when(itemRequestService.getItemRequest(1L, 1L)).thenReturn(itemRequestDto);
        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemRequestDto)));
    }

    @Test
    @DisplayName("ItemRequestController_createItemRequest")
    void createItemRequest_whenInvoked_thenHasCorrectResponse() throws Exception {
        when(itemRequestService.createItemRequest(1L, newItemRequestDto)).thenReturn(itemRequestDto);
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(newItemRequestDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class));
    }
}