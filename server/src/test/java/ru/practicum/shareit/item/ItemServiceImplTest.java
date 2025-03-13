package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dal.CommentRepository;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCommentsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dal.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void createItemWithoutRequest_returnItemDto() {
        User user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("12345@mail.ru");

        ItemDto itemDto = new ItemDto();
        itemDto.setName("itemDto");
        itemDto.setDescription("description");
        itemDto.setId(2L);

        Item item = ItemMapper.toItem(itemDto, user);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto actualItemDto = itemService.createItem(itemDto, user.getId());

        assertEquals(actualItemDto.getId(), item.getId());
        assertEquals(actualItemDto.getName(), item.getName());

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void createItemWithRequest_returnItemDto() {
        User user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("12345@mail.ru");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(3L);
        itemRequest.setRequester(user);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("itemDto");
        itemDto.setDescription("description");
        itemDto.setId(2L);
        itemDto.setRequestId(itemRequest.getId());

        Item item = new Item();
        item.setId(0L);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);
        item.setRequest(itemRequest);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto actualItemDto = itemService.createItem(itemDto, user.getId());

        assertEquals(actualItemDto.getId(), item.getId());
        assertEquals(actualItemDto.getName(), item.getName());
        assertEquals(actualItemDto.getRequestId(), item.getRequest().getId());

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void createItemFailed_throwException() {
        User user = new User();
        user.setId(1L);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("itemDto");
        itemDto.setDescription("description");
        itemDto.setId(2L);
        itemDto.setRequestId(5L);

        assertThrows(NotFoundException.class, () -> itemService.createItem(itemDto, user.getId()));

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void getItem_returnItemCommentsDto() {
        User user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("12345@mail.ru");

        Item item = new Item();
        item.setId(0L);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);
        item.setRequest(null);

        CommentDto commentDto = new CommentDto();
        commentDto.setId(3L);
        commentDto.setText("someText");

        Booking booking = new Booking();

        ItemCommentsDto itemCommentsDto = ItemMapper.toItemCommentsDto(item, List.of(commentDto));

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn((List.of()));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfter(anyLong(),
                eq(Status.APPROVED), any(), eq(Sort.by("start").descending()))).thenReturn(Optional.of(booking));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfter(anyLong(),
                eq(Status.APPROVED), any(), eq(Sort.by("start")))).thenReturn(Optional.of(booking));

        ItemCommentsDto actual = itemService.getItem(user.getId(), item.getId());

        assertEquals(actual.getId(), itemCommentsDto.getId());
    }

    @Test
    void deleteItem_returnNothing() {
        Item item = new Item();
        item.setId(0L);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        itemService.deleteItem(item.getId());

        verify(itemRepository, times(1)).delete(item);
    }

    @Test
    void deleteItem_whenItemNotFound_throwException() {
        assertThrows(NotFoundException.class, () -> itemService.deleteItem(1L));

        verify(itemRepository, never()).delete(any(Item.class));
    }

    @Test
    void updateItem_whenItemIsValid() {
        User user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("12345@mail.ru");

        Item item = new Item();
        item.setId(0L);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("itemDto");
        itemDto.setDescription("description1");
        itemDto.setRequestId(5L);
        itemDto.setAvailable(true);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        ItemDto actual = itemService.updateItem(itemDto, user.getId(), item.getId());

        assertEquals(actual.getId(), item.getId());
        assertEquals(actual.getName(), itemDto.getName());
        assertEquals(actual.getDescription(), itemDto.getDescription());

        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void updateItem_wrongOwner_throwValidationException() {
        User user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("12345@mail.ru");

        User user1 = new User();
        user1.setId(2L);
        user1.setName("name1");
        user1.setEmail("1234567@mail.ru");

        Item item = new Item();
        item.setId(0L);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user1);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("itemDto");
        itemDto.setDescription("description1");
        itemDto.setRequestId(5L);
        itemDto.setAvailable(true);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        assertThrows(ValidationException.class, () -> itemService.updateItem(itemDto, user.getId(), item.getId()));

        verify(itemRepository, never()).save(item);
    }

    @Test
    void getAllItemsOfUser_whenUserIsFound() {

        User user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("12345@mail.ru");

        Item item = new Item();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findByOwnerId(user.getId())).thenReturn(List.of(item));

        Collection<ItemDto> actual = itemService.getAllItemsOfUser(user.getId());

        assertNotNull(actual);
        assertEquals(1, actual.size());

        verify(itemRepository, times(1)).findByOwnerId(anyLong());
    }

    @Test
    void getAllItemsOfUser_whenUserIsNotFound() {
        assertThrows(NotFoundException.class, () -> itemService.getAllItemsOfUser(anyLong()));
        verify(itemRepository, never()).findByOwnerId(anyLong());
    }

    @Test
    void searchItemWithText() {
        Item item = new Item();

        when(itemRepository.findItemByText(anyString())).thenReturn(List.of(item));

        Collection<ItemDto> col = itemService.searchItems(1L, "aaa");

        assertNotNull(col);
        assertEquals(1, col.size());
    }

    @Test
    void searchItemWithoutText() {
        Collection<ItemDto> list = itemService.searchItems(1L, "");

        assertNotNull(list);
        assertEquals(0, list.size());
        verify(itemRepository, never()).findItemByText("");
    }

    @Test
    void createComment() {
        User user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("12345@mail.ru");

        Item item = new Item();
        item.setId(0L);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("someText");
        commentDto.setAuthorName(user.getName());

        Booking booking = new Booking();
        booking.setId(2L);
        booking.setStart(null);
        booking.setEnd(null);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(null);

        Comment comment = CommentMapper.toComment(user, item, commentDto);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(), anyLong(), eq(Status.APPROVED), any()))
                .thenReturn(Optional.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto actual = itemService.createComment(user.getId(), item.getId(), commentDto);

        assertEquals(actual.getText(), commentDto.getText());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void createComment_wrongBooker() {
        User user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("12345@mail.ru");

        User user1 = new User();
        user1.setId(5L);
        user1.setName("name");
        user1.setEmail("12345@mail.ru");

        Item item = new Item();
        item.setId(0L);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("someText");
        commentDto.setAuthorName(user.getName());

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(), anyLong(), eq(Status.APPROVED), any()))
                .thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> itemService.createComment(5L, 0L, commentDto));
    }
}
