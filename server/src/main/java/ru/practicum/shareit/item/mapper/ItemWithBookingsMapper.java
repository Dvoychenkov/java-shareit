package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.booking.dto.BookingTimeDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = CommentMapper.class)
public interface ItemWithBookingsMapper {

    @Mapping(target = "id", source = "item.id")
    ItemWithBookingsDto toItemWithBookingsDto(Item item, Booking lastBooking, Booking nextBooking, Collection<Comment> comments);

    default ItemWithBookingsDto toItemWithBookingsDto(Item item, Collection<Comment> comments) {
        return toItemWithBookingsDto(item, null, null, comments);
    }

    default BookingTimeDto map(Booking b) {
        if (b == null) {
            return null;
        }
        return new BookingTimeDto(b.getStart(), b.getEnd());
    }
}
