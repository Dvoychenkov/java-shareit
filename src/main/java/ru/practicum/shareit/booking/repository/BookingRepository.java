package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    /*
        Запросы по бронирующему
    */
    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByBooker_IdOrderByStartDesc(Long bookerId);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByBooker_IdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(Long bookerId,
                                                                                             LocalDateTime start,
                                                                                             LocalDateTime end);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByBooker_IdAndEndLessThanOrderByStartDesc(Long bookerId, LocalDateTime end);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByBooker_IdAndStartGreaterThanOrderByStartDesc(Long bookerId, LocalDateTime start);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByBooker_IdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    /*
        Запросы по владельцу вещи
    */
    @Query("""
            select b
            from Booking b
            join fetch b.item i
            join fetch b.booker u
            where i.owner = :ownerId
            order by b.start desc
            """)
    List<Booking> findAllByOwner(@Param("ownerId") Long ownerId);

    @Query("""
            select b
            from Booking b
            join fetch b.item i
            join fetch b.booker u
            where i.owner = :ownerId
                and b.start <= :now
                and b.end >= :now
            order by b.start desc
            """)
    List<Booking> findCurrentByOwner(@Param("ownerId") Long ownerId,
                                     @Param("now") LocalDateTime now);

    @Query("""
            select b
            from Booking b
            join fetch b.item i
            join fetch b.booker u
            where i.owner = :ownerId
                and b.end < :now
            order by b.start desc
            """)
    List<Booking> findPastByOwner(@Param("ownerId") Long ownerId,
                                  @Param("now") LocalDateTime now);

    @Query("""
            select b
            from Booking b
            join fetch b.item i
            join fetch b.booker u
            where i.owner = :ownerId
                and b.start > :now
            order by b.start desc
            """)
    List<Booking> findFutureByOwner(@Param("ownerId") Long ownerId,
                                    @Param("now") LocalDateTime now);

    @Query("""
            select b
            from Booking b
            join fetch b.item i
            join fetch b.booker u
            where i.owner = :ownerId
                and b.status = :status
            order by b.start desc
            """)
    List<Booking> findByOwnerAndStatus(@Param("ownerId") Long ownerId,
                                       @Param("status") BookingStatus status);
}
