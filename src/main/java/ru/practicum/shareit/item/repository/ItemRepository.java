package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findAllByOwner(Long ownerId);

    @Query("""
            select i from Item i
            where i.available = :isAvailable
            and (
                upper(i.name) like upper(concat('%', :nameText, '%'))
            	or upper(i.description) like upper(concat('%', :descText, '%'))
            )
            """)
    Collection<Item> search(@Param("nameText") String nameText,
                            @Param("descText") String descText,
                            @Param("isAvailable") boolean isAvailable);
}