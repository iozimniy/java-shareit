package ru.practicum.shareit.item.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDTOWithBookings;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ItemServiceImpl implements ItemService {

    UserService userService;
    ItemRepository itemRepository;
    ItemMapper itemMapper;
    UserRepository userRepository;
    BookingRepository bookingRepository;

    @Override
    @Transactional
    public ItemDto create(Long userId, ItemDto itemDto) throws NotFoundException, ValidationException {
        validateUserId(userId);
        validateItemDtoForCreate(itemDto);
        Item item = itemMapper.toItem(itemDto, userRepository.findById(userId).get());
        item = itemRepository.save(item);
        return itemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto update(Long itemId, Long userId, ItemDto itemDto) throws NotFoundException, ValidationException {
        validateUserId(userId);
        validateItemDtoForUpdate(itemDto);
        Item oldItem = itemRepository.findByIdAndOwnerId(itemId, userId)
                .orElseThrow(() -> new NotFoundException("У пользователя c id " + userId + " нет вещи с id " + itemId));

        Item item = updateItem(oldItem, itemMapper.toItem(itemDto, userRepository.findById(userId).get()));
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItem(Long itemId) throws NotFoundException {
        return itemRepository.findById(itemId)
                .map(itemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена по id"));
    }

    @Override
    public Collection<ItemDTOWithBookings> getUserItems(Long userId) throws NotFoundException {
        validateUserId(userId);
        List<Item> ownersItem = itemRepository.findAllByOwnerId(userId);
        return ownersItem.stream()
                .map(item -> mapToItemWithBooking(item))
                .collect(Collectors.toList());
    }

    private ItemDTOWithBookings mapToItemWithBooking(Item item) {
        List<Booking> itemBookings = bookingRepository.findAllByItemId(item.getId());

        return itemMapper.toItemDTOWithBooking(item,
                getLastBookingEndDate(itemBookings).orElse(LocalDateTime.now()),
                getNextBookingStartDate(itemBookings).orElse(LocalDateTime.now()));
    }

    @Override
    public Collection<ItemDto> search(String text) {
        if (!StringUtils.hasText(text)) {
            return Collections.EMPTY_LIST;
        }
        return itemRepository.findAllByNameOrDescriptionLike(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private Item updateItem(Item oldItem, Item newItem) {
        newItem.setId(oldItem.getId());
        newItem.setRequestId(oldItem.getRequestId());

        if (newItem.getName() == null) {
            newItem.setName(oldItem.getName());
        }

        if (newItem.getDescription() == null) {
            newItem.setDescription(oldItem.getDescription());
        }

        if (newItem.getIsAvailable() == null) {
            newItem.setIsAvailable(oldItem.getIsAvailable());
        }

        return newItem;
    }

    public Item getItemForBooking(Long itemId) throws NotFoundException, ValidationException {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id " + itemId));

        if (!item.getIsAvailable()) {
            throw new ValidationException("Вещь не доступна для бронирования");
        }

        return item;
    }

    private void validateUserId(Long userId) throws NotFoundException {
        userService.validateId(userId);
    }

    private void validateItemDtoForCreate(ItemDto itemDto) throws ValidationException {
        if (itemDto.getName() == null || itemDto.getDescription() == null) {
            throw new ValidationException("У вещи должно быть название и описание.");
        } else if (itemDto.getAvailable() == null)
            throw new ValidationException("Необходимо указать доступность вещи для бронирования");
    }

    private void validateItemDtoForUpdate(ItemDto itemDto) throws ValidationException {
        if (itemDto.getName() != null && itemDto.getName().isEmpty()) {
            throw new ValidationException("У вещи должно быть название и описанине.");
        }

        if (itemDto.getDescription() != null && itemDto.getDescription().isEmpty()) {
            throw new ValidationException("У вещи должно быть название и описанине.");
        }
    }

    private Optional<LocalDateTime> getLastBookingEndDate(List<Booking> bookings) {
        return bookings.stream().map(booking -> booking.getEndDate())
                .filter(date -> date.isBefore(LocalDateTime.now()))
                .max(LocalDateTime::compareTo);
    }

    private Optional<LocalDateTime> getNextBookingStartDate(List<Booking> bookings) {
        return bookings.stream().map(booking -> booking.getStartDate())
                .filter(date -> date.isAfter(LocalDateTime.now()))
                .min(LocalDateTime::compareTo);
    }
}
