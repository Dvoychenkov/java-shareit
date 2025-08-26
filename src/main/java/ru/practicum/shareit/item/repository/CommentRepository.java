package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Collection<Comment> findAllByItem_IdOrderByCreatedDesc(Long itemId);

    Collection<Comment> findAllByItem_IdInOrderByCreatedDesc(Collection<Long> itemIds);
}