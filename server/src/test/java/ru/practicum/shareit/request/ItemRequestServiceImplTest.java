package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.request.dal.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;


    @Test
    @DisplayName("ItemRequestServiceImpl_createItemRequest")
    void createItemRequest_whenIsOk_returnItemRequestDto() {
        User user = new User();

        NewItemRequestDto newItemRequestDto = new NewItemRequestDto();
        newItemRequestDto.setDescription("description");

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(user, newItemRequestDto);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto itemRequestDto = itemRequestService.createItemRequest(1L, newItemRequestDto);

        assertEquals(newItemRequestDto.getDescription(), itemRequestDto.getDescription());
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    @DisplayName("ItemRequestServiceImpl_getAllItemRequestsByUser")
    void getAllItemRequestsByUser_whenIsOk_returnListItemRequestDto() {
        User user = new User();
        user.setId(1L);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequester(user);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterId(anyLong(),
                eq(Sort.by("created").descending()))).thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequest(itemRequest)).thenReturn(List.of());

        List<ItemRequestDto> itemRequestDtoList = itemRequestService.getAllItemRequestsByUser(user.getId());

        assertNotNull(itemRequestDtoList);
        assertEquals(1, itemRequestDtoList.size());

        verify(itemRequestRepository, times(1)).findAllByRequesterId(anyLong(),
                eq(Sort.by("created").descending()));
    }

    @Test
    @DisplayName("ItemRequestServiceImpl_getAllItemRequests")
    void getAllItemRequests_whenIsOk_returnListItemRequestDto() {
        User user = new User();
        user.setId(1L);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequester(user);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterIdNot(user.getId(),
                Sort.by("created").descending())).thenReturn(List.of(itemRequest));

        List<ItemRequestDto> itemRequestDtoList = itemRequestService.getAllItemRequests(user.getId());

        assertNotNull(itemRequestDtoList);
        assertEquals(1, itemRequestDtoList.size());

        verify(itemRequestRepository, times(1)).findAllByRequesterIdNot(user.getId(),
                Sort.by("created").descending());
    }

    @Test
    @DisplayName("ItemRequestServiceImpl_getItemRequest")
    void getItemRequest_whenIsOk_returnItemRequestDto() {
        User user = new User();
        user.setId(1L);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(2L);
        itemRequest.setRequester(user);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequest(itemRequest)).thenReturn(List.of());

        ItemRequestDto itemRequestDto = itemRequestService.getItemRequest(user.getId(), itemRequest.getId());

        assertEquals(itemRequest.getId(), itemRequestDto.getId());
    }

    @Test
    @DisplayName("ItemRequestServiceImpl_createItemRequestException")
    void createItemRequest_ThrowsException() {
        NewItemRequestDto newItemRequestDto = new NewItemRequestDto();

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.createItemRequest(anyLong(), newItemRequestDto));
    }

    @Test
    @DisplayName("ItemRequestServiceImpl_getAllItemRequestsByUserException")
    void getAllItemRequestsByUser_ThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getAllItemRequestsByUser(anyLong()));
    }

    @Test
    @DisplayName("ItemRequestServiceImpl_getAllItemRequestsException")
    void getAllItemRequests_ThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getAllItemRequests(anyLong()));
    }

    @Test
    @DisplayName("ItemRequestServiceImpl_getItemRequestException")
    void getItemRequest_ThrowException() {
        User user = new User();
        user.setId(1L);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(2L);

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequest(user.getId(), itemRequest.getId()));
    }
}
