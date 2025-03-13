package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.service.interfaces.BookingService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private BookingDto bookingDto;

    private NewBookingDto newBookingDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        bookingDto = new BookingDto();
        bookingDto.setStatus(Status.WAITING);
        bookingDto.setItem(null);
        bookingDto.setBooker(null);
        bookingDto.setStart(null);
        bookingDto.setEnd(null);
        bookingDto.setId(1L);

        newBookingDto = new NewBookingDto();
        newBookingDto.setItemId(2L);
        newBookingDto.setStart(null);
        newBookingDto.setEnd(null);
    }

    @Test
    void createBooking_whenInvoked_thenHasCorrectResponse() throws Exception {
        when(bookingService.createBooking(1L, newBookingDto)).thenReturn(bookingDto);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(newBookingDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));
    }

    @Test
    void getBooking_whenInvoked_thenHasCorrectResponse() throws Exception {
        when(bookingService.getBookingById(eq(1L), eq(1L))).thenReturn(bookingDto);
        mvc.perform(get("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));
    }

    @Test
    void getAllBookingsUser_whenInvoked_thenHasCorrectResponse() throws Exception {
        when(bookingService.getAllBookingsUser(eq(1L), any())).thenReturn(List.of(bookingDto));
        mvc.perform(get("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));
    }

    @Test
    void getAllBookingsOwner_whenInvoked_thenHasCorrectResponse() throws Exception {
        when(bookingService.getAllBookingsOwner(eq(1L), any())).thenReturn(List.of(bookingDto));
        mvc.perform(get("/bookings/owner")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));
    }

    @Test
    void confirmationBooking_whenInvoked_thenHasCorrectResponse() throws Exception {
        when(bookingService.confirmationBooking(1L, 1L, true)).thenReturn(bookingDto);
        mvc.perform(patch("/bookings/1?approved=true")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));
    }
}