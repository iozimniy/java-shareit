package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.request.model.ItemRequest;

/**
 * TODO Sprint add-controllers.
 */

@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id", nullable = false)
    Long id;
    String name;
    String description;
    @Column(name = "is_available", nullable = false)
    Boolean isAvailable;
    @Column(name = "owner_id", nullable = false)
    //@ManyToOne
    //@JoinColumn(name = "user_id")
    Long ownerId;
    Long requestId;
}
