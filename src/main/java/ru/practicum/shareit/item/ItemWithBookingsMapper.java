package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.booking.dto.BookingTimeDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Optional;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemWithBookingsMapper {

    @Mapping(target = "id", source = "item.id")
    ItemWithBookingsDto toDto(Item item, Booking lastBooking, Booking nextBooking);

    default ItemWithBookingsDto toDto(Item item, Optional<Booking> lastBooking, Optional<Booking> nextBooking) {
        return toDto(item, lastBooking.orElse(null), nextBooking.orElse(null));
    }

    default BookingTimeDto map(Booking b) {
        if (b == null) {
            return null;
        }
        return new BookingTimeDto(b.getStart(), b.getEnd());
    }
}
