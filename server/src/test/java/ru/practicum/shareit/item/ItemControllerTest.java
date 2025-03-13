package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCommentsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.interfaces.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private CommentDto commentDto;

    private ItemDto itemDto;

    private ItemCommentsDto itemCommentsDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("name");
        itemDto.setRequestId(2L);
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        itemDto.setOwner(null);

        commentDto = new CommentDto();
        commentDto.setId(2L);
        commentDto.setAuthorName("name");
        commentDto.setCreated(null);
        commentDto.setText("someText");

        itemCommentsDto = new ItemCommentsDto();
        itemCommentsDto.setId(3L);
        itemCommentsDto.setAvailable(true);
        itemCommentsDto.setName("name");
        itemCommentsDto.setComments(null);
        itemCommentsDto.setDescription("description");

    }

    @Test
    void getAllItemsOfUser_whenInvoked_thenHasCorrectResponse() throws Exception {
        when(itemService.getAllItemsOfUser(1L)).thenReturn(List.of(itemDto));
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto))));
    }

    @Test
    void getItem_whenInvoked_thenHasCorrectResponse() throws Exception {
        when(itemService.getItem(1L, 1L)).thenReturn(itemCommentsDto);
        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemCommentsDto)));
    }

    @Test
    void getItemByText_whenInvoked_thenHasCorrectResponse() throws Exception {
        when(itemService.searchItems(eq(1L), any())).thenReturn(List.of(itemDto));
        mvc.perform(get("/items/search?text=text")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto))));
    }

    @Test
    void createItem_whenInvoked_thenHasCorrectResponse() throws Exception {
        when(itemService.createItem(itemDto, 1L)).thenReturn(itemDto);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDto)));
    }

    @Test
    void createComment_whenInvoked_thenHasCorrectResponse() throws Exception {
        when(itemService.createComment(1L, 1L, commentDto)).thenReturn(commentDto);
        mvc.perform(post("/items/1/comment").header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentDto)));
    }

    @Test
    void updateItem_whenInvoked_thenHasCorrectResponse() throws Exception {
        when(itemService.updateItem(itemDto, 1L, 1L)).thenReturn(itemDto);
        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDto)));
    }
}