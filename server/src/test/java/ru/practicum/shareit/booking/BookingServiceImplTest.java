package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.State;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    BookingServiceImpl bookingService;

    @Test
    void createBooking_correctBooking() {
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

        NewBookingDto newBookingDto = new NewBookingDto();
        newBookingDto.setItemId(item.getId());
        newBookingDto.setStart(LocalDateTime.now());
        newBookingDto.setEnd(LocalDateTime.now().plusHours(2));

        Booking booking = BookingMapper.toBooking(user, item, newBookingDto);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto actual = bookingService.createBooking(anyLong(), newBookingDto);

        assertEquals(actual.getItem().getId(), newBookingDto.getItemId());
        assertEquals(actual.getStart(), newBookingDto.getStart());
        assertEquals(actual.getEnd(), newBookingDto.getEnd());

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBooking_itemIsNotAvailable() {
        User user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("12345@mail.ru");

        Item item = new Item();
        item.setId(0L);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(false);
        item.setOwner(user);

        NewBookingDto newBookingDto = new NewBookingDto();
        newBookingDto.setItemId(item.getId());
        newBookingDto.setStart(LocalDateTime.now());
        newBookingDto.setEnd(LocalDateTime.now().plusHours(2));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(anyLong(), newBookingDto));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_wrongEqualTime() {
        User user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("12345@mail.ru");

        Item item = new Item();
        item.setId(0L);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(false);
        item.setOwner(user);

        NewBookingDto newBookingDto = new NewBookingDto();
        newBookingDto.setItemId(item.getId());
        newBookingDto.setStart(LocalDateTime.now());
        newBookingDto.setEnd(LocalDateTime.now());

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(anyLong(), newBookingDto));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_wrongTime() {
        User user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("12345@mail.ru");

        Item item = new Item();
        item.setId(0L);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(false);
        item.setOwner(user);

        NewBookingDto newBookingDto = new NewBookingDto();
        newBookingDto.setItemId(item.getId());
        newBookingDto.setStart(LocalDateTime.now().plusHours(2));
        newBookingDto.setEnd(LocalDateTime.now());

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(anyLong(), newBookingDto));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void confirmationBookingApproved() {
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

        Booking booking = new Booking();
        booking.setId(2L);
        booking.setStatus(Status.WAITING);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now());
        booking.setBooker(user);
        booking.setItem(item);

        when(bookingRepository.findByIdAndItem_OwnerId(anyLong(), anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDto actual = bookingService.confirmationBooking(anyLong(), anyLong(), true);

        assertEquals(actual.getStatus(), booking.getStatus());
        assertEquals(actual.getId(), booking.getId());
        assertEquals(actual.getStatus(), Status.APPROVED);

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void confirmationBookingRejected() {
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

        Booking booking = new Booking();
        booking.setId(2L);
        booking.setStatus(Status.WAITING);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now());
        booking.setBooker(user);
        booking.setItem(item);

        when(bookingRepository.findByIdAndItem_OwnerId(anyLong(), anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDto actual = bookingService.confirmationBooking(anyLong(), anyLong(), false);

        assertEquals(actual.getStatus(), booking.getStatus());
        assertEquals(actual.getId(), booking.getId());
        assertEquals(actual.getStatus(), Status.REJECTED);

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void confirmationBookingStatusNotWaiting() {
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

        Booking booking = new Booking();
        booking.setId(2L);
        booking.setStatus(Status.CANCELED);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now());
        booking.setBooker(user);
        booking.setItem(item);

        assertThrows(ValidationException.class, () -> bookingService.confirmationBooking(user.getId(), booking.getId(), true));
    }

    @Test
    void confirmationBookingWrongTime() {
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

        Booking booking = new Booking();
        booking.setId(2L);
        booking.setStatus(Status.CANCELED);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(5));
        booking.setBooker(user);
        booking.setItem(item);

        assertThrows(ValidationException.class, () -> bookingService.confirmationBooking(user.getId(), booking.getId(), true));
    }

    @Test
    void getBookingByIdCorrect() {
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

        Booking booking = new Booking();
        booking.setId(2L);
        booking.setStatus(Status.CANCELED);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(5));
        booking.setBooker(user);
        booking.setItem(item);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDto actual = bookingService.getBookingById(user.getId(), booking.getId());

        assertEquals(actual.getId(), booking.getId());
        assertEquals(actual.getBooker().getId(), booking.getBooker().getId());
    }

    @Test
    void getBookingNotFoundUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(1L, 2L));
    }

    @Test
    void getBookingNotFoundBooking() {
        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(1L, 2L));
    }

    @Test
    void getBookingByIdFailed() {
        User user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("12345@mail.ru");

        User user2 = new User();
        user2.setId(3L);
        user2.setName("name");
        user2.setEmail("12345@mail.ru");

        Item item = new Item();
        item.setId(0L);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user2);

        Booking booking = new Booking();
        booking.setId(2L);
        booking.setStatus(Status.CANCELED);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(5));
        booking.setBooker(user2);
        booking.setItem(item);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.getBookingById(user.getId(), booking.getId()));
    }

    @Test
    void getAllBookingsOfUser_whenStateALL() {
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

        Booking booking = new Booking();
        booking.setId(2L);
        booking.setStatus(Status.WAITING);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(5));
        booking.setBooker(user);
        booking.setItem(item);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerId(anyLong(), any(Sort.class))).thenReturn(List.of(booking));

        Collection<BookingDto> collection = bookingService.getAllBookingsUser(user.getId(), State.ALL);

        assertNotNull(collection);
        assertEquals(1, collection.size());

        verify(bookingRepository, times(1)).findByBookerId(user.getId(), Sort.by("start"));
    }

    @Test
    void getAllBookingsOfUser_whenStateWaiting() {
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

        Booking booking = new Booking();
        booking.setId(2L);
        booking.setStatus(Status.WAITING);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(5));
        booking.setBooker(user);
        booking.setItem(item);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStatus(user.getId(), Status.WAITING, Sort.by("start"))).thenReturn(List.of(booking));

        Collection<BookingDto> collection = bookingService.getAllBookingsUser(user.getId(), State.WAITING);

        assertNotNull(collection);
        assertEquals(1, collection.size());

        verify(bookingRepository, times(1)).findByBookerIdAndStatus(user.getId(), Status.WAITING, Sort.by("start"));
    }

    @Test
    void getAllBookingsOfUser_whenStateCurrent() {
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

        Booking booking = new Booking();
        booking.setId(2L);
        booking.setStatus(Status.WAITING);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(5));
        booking.setBooker(user);
        booking.setItem(item);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(anyLong(), any(LocalDateTime.class),
                any(LocalDateTime.class), any(Sort.class))).thenReturn(List.of(booking));

        Collection<BookingDto> collection = bookingService.getAllBookingsUser(user.getId(), State.CURRENT);

        assertNotNull(collection);
        assertEquals(1, collection.size());

        verify(bookingRepository, times(1)).findByBookerIdAndStartBeforeAndEndAfter(anyLong(), any(LocalDateTime.class),
                any(LocalDateTime.class), any(Sort.class));
    }

    @Test
    void getAllBookingsOfUser_whenStateRejected() {
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

        Booking booking = new Booking();
        booking.setId(2L);
        booking.setStatus(Status.WAITING);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(5));
        booking.setBooker(user);
        booking.setItem(item);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStatusOrBookerIdAndStatus(user.getId(), Status.REJECTED, user.getId(),
                Status.CANCELED, Sort.by("start"))).thenReturn(List.of(booking));

        Collection<BookingDto> collection = bookingService.getAllBookingsUser(user.getId(), State.REJECTED);

        assertNotNull(collection);
        assertEquals(1, collection.size());

        verify(bookingRepository, times(1)).findByBookerIdAndStatusOrBookerIdAndStatus(user.getId(), Status.REJECTED, user.getId(),
                Status.CANCELED, Sort.by("start"));
    }

    @Test
    void getAllBookingsOfUser_whenStatePast() {
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

        Booking booking = new Booking();
        booking.setId(2L);
        booking.setStatus(Status.WAITING);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(5));
        booking.setBooker(user);
        booking.setItem(item);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndEndBefore(anyLong(), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(booking));

        Collection<BookingDto> collection = bookingService.getAllBookingsUser(user.getId(), State.PAST);

        assertNotNull(collection);
        assertEquals(1, collection.size());

        verify(bookingRepository, times(1)).findByBookerIdAndEndBefore(anyLong(),
                any(LocalDateTime.class), any(Sort.class));
    }

    @Test
    void getAllBookingsOfUser_whenStateFuture() {
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

        Booking booking = new Booking();
        booking.setId(2L);
        booking.setStatus(Status.WAITING);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(5));
        booking.setBooker(user);
        booking.setItem(item);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStartAfter(anyLong(), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(booking));

        Collection<BookingDto> collection = bookingService.getAllBookingsUser(user.getId(), State.FUTURE);

        assertNotNull(collection);
        assertEquals(1, collection.size());

        verify(bookingRepository, times(1)).findByBookerIdAndStartAfter(anyLong(),
                any(LocalDateTime.class), any(Sort.class));
    }

    @Test
    void getAllBookingsOwner_whenStateAll() {
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

        Booking booking = new Booking();
        booking.setId(2L);
        booking.setStatus(Status.WAITING);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(5));
        booking.setBooker(user);
        booking.setItem(item);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByItem_OwnerId(user.getId(), Sort.by("start")))
                .thenReturn(List.of(booking));

        Collection<BookingDto> collection = bookingService.getAllBookingsOwner(user.getId(), State.ALL);

        assertNotNull(collection);
        assertEquals(1, collection.size());

        verify(bookingRepository, times(1)).findByItem_OwnerId(user.getId(), Sort.by("start"));
    }

    @Test
    void getAllBookingsOwner_whenStateWaiting() {
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

        Booking booking = new Booking();
        booking.setId(2L);
        booking.setStatus(Status.WAITING);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(5));
        booking.setBooker(user);
        booking.setItem(item);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByItem_OwnerIdAndStatus(user.getId(), Status.WAITING, Sort.by("start")))
                .thenReturn(List.of(booking));

        Collection<BookingDto> collection = bookingService.getAllBookingsOwner(user.getId(), State.WAITING);

        assertNotNull(collection);
        assertEquals(1, collection.size());

        verify(bookingRepository, times(1)).findByItem_OwnerIdAndStatus(user.getId(),
                Status.WAITING, Sort.by("start"));
    }

    @Test
    void getAllBookingsOwner_whenStateCurrent() {
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

        Booking booking = new Booking();
        booking.setId(2L);
        booking.setStatus(Status.WAITING);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(5));
        booking.setBooker(user);
        booking.setItem(item);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByItem_OwnerIdAndStartBeforeAndEndAfter(anyLong(), any(LocalDateTime.class),
                any(LocalDateTime.class), any(Sort.class))).thenReturn(List.of(booking));

        Collection<BookingDto> collection = bookingService.getAllBookingsOwner(user.getId(), State.CURRENT);

        assertNotNull(collection);
        assertEquals(1, collection.size());

        verify(bookingRepository, times(1)).findByItem_OwnerIdAndStartBeforeAndEndAfter(anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Sort.class));
    }

    @Test
    void getAllBookingsOwner_whenStateRejected() {
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

        Booking booking = new Booking();
        booking.setId(2L);
        booking.setStatus(Status.WAITING);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(5));
        booking.setBooker(user);
        booking.setItem(item);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByItem_OwnerIdAndStatusOrItem_OwnerIdAndStatus(user.getId(), Status.REJECTED,
                user.getId(), Status.CANCELED, Sort.by("start"))).thenReturn(List.of(booking));

        Collection<BookingDto> collection = bookingService.getAllBookingsOwner(user.getId(), State.REJECTED);

        assertNotNull(collection);
        assertEquals(1, collection.size());

        verify(bookingRepository, times(1)).findByItem_OwnerIdAndStatusOrItem_OwnerIdAndStatus(user.getId(),
                Status.REJECTED, user.getId(), Status.CANCELED, Sort.by("start"));
    }

    @Test
    void getAllBookingsOwner_whenStatePast() {
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

        Booking booking = new Booking();
        booking.setId(2L);
        booking.setStatus(Status.WAITING);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(5));
        booking.setBooker(user);
        booking.setItem(item);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByItem_OwnerIdAndEndBefore(anyLong(), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(booking));

        Collection<BookingDto> collection = bookingService.getAllBookingsOwner(user.getId(), State.PAST);

        assertNotNull(collection);
        assertEquals(1, collection.size());

        verify(bookingRepository, times(1)).findByItem_OwnerIdAndEndBefore(anyLong(),
                any(LocalDateTime.class), any(Sort.class));
    }

    @Test
    void getAllBookingsOwner_whenStateFuture() {
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

        Booking booking = new Booking();
        booking.setId(2L);
        booking.setStatus(Status.WAITING);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(5));
        booking.setBooker(user);
        booking.setItem(item);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByItem_OwnerIdAndStartAfter(anyLong(), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(booking));

        Collection<BookingDto> collection = bookingService.getAllBookingsOwner(user.getId(), State.FUTURE);

        assertNotNull(collection);
        assertEquals(1, collection.size());

        verify(bookingRepository, times(1)).findByItem_OwnerIdAndStartAfter(anyLong(),
                any(LocalDateTime.class), any(Sort.class));
    }
}