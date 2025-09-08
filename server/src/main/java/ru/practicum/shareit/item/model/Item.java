package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "items")
@EqualsAndHashCode(of = "id")
@ToString
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "is_available", nullable = false)
    private boolean available;

    @Column(name = "owner_id", nullable = false)
    private Long owner;

    @Column(name = "request_id")
    private Long request;
}
