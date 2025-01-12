package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByIdAndOwnerId(Long itemId, Long ownerId);

    Boolean existsByIdAndOwnerId(Long itemId, Long ownerId);

    List<Item> findAllByOwnerId(Long ownerId);

    Boolean existsByOwnerId(Long ownerId);

    @Query("select it from Item as it where (UPPER(it.name) like %?1% or UPPER(it.description) like %?1%) " +
            "and it.isAvailable = true")
    List<Item> findAllByNameOrDescriptionLike(String text);
}
