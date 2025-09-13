package ru.practicum.shareit.booking.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "item", source = "item")
    @Mapping(target = "booker", source = "booker")
    @Mapping(target = "status", ignore = true)
    Booking toBooking(NewBookingDto dto, Item item, User booker);

    @Mapping(target = "item", source = "booking.item")
    @Mapping(target = "booker", source = "booking.booker")
    BookingDto toBookingDto(Booking booking);

    default BookingItemDto map(Item item) {
        return new BookingItemDto(item.getId(), item.getName());
    }

    default BookingBookerDto map(User user) {
        return new BookingBookerDto(user.getId());
    }
}
