package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCommentsDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemCommentsDtoTest {

    @Autowired
    JacksonTester<ItemCommentsDto> jacksonTester;

    @Test
    void testSerializeItemCommentsDto() throws Exception {
        ItemCommentsDto itemCommentsDto = new ItemCommentsDto();
        itemCommentsDto.setId(1L);
        itemCommentsDto.setName("name");
        itemCommentsDto.setDescription("description");
        itemCommentsDto.setAvailable(true);
        itemCommentsDto.setOwner(new UserDto());
        itemCommentsDto.setLastBooking(LocalDateTime.now());
        itemCommentsDto.setNextBooking(LocalDateTime.now());
        itemCommentsDto.setComments(List.of(new CommentDto(), new CommentDto()));

        JsonContent<ItemCommentsDto> result = jacksonTester.write(itemCommentsDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.owner");
        assertThat(result).hasJsonPath("$.lastBooking");
        assertThat(result).hasJsonPath("$.nextBooking");
        assertThat(result).hasJsonPath("$.comments");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemCommentsDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemCommentsDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathArrayValue("$.comments").isNotEmpty();
        assertThat(result).hasJsonPathValue("$.name");
        assertThat(result).hasJsonPathValue("$.owner");
        assertThat(result).hasJsonPathValue("$.lastBooking");
        assertThat(result).hasJsonPathValue("$.nextBooking");
    }
}