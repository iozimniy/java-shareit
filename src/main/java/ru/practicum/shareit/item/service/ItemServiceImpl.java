package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ItemServiceImpl implements ItemService {

    private static final Comparator<Booking> comparatorBookingEndDate = ItemServiceImpl::compareBookingEndDate;
    private static final Comparator<Booking> comparatorBookingStartDate = ItemServiceImpl::compareBookingStartDate;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    private static int compareBookingEndDate(Booking o1, Booking o2) {
        if (o1.getEndDate().isAfter(o2.getEndDate())) {
            return 1;
        } else if (o1.getEndDate().isBefore(o2.getEndDate())) {
            return -1;
        } else {
            return 0;
        }
    }

    private static int compareBookingStartDate(Booking o1, Booking o2) {
        if (o1.getStartDate().isAfter(o2.getStartDate())) {
            return 1;
        } else if (o1.getStartDate().isBefore(o2.getStartDate())) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    @Transactional
    public ItemDto create(Long userId, ItemDto itemDto) throws NotFoundException, ValidationException {
        log.debug("Получен запрос от пользователя с id {} на создание вещи", userId, itemDto);
        validateUserId(userId);
        validateItemDtoForCreate(itemDto);
        Item item = itemMapper.toItem(itemDto, userRepository.findById(userId).get());
        item = itemRepository.save(item);
        return itemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto update(Long itemId, Long userId, ItemDto itemDto) throws NotFoundException, ValidationException {
        log.debug("Получен запрос от пользователя с id {} на изменение вещи с id {} в виде {}", userId, itemId, itemDto);
        validateUserId(userId);
        validateItemDtoForUpdate(itemDto);
        Item oldItem = itemRepository.findByIdAndOwnerId(itemId, userId)
                .orElseThrow(() -> new NotFoundException("У пользователя c id " + userId + " нет вещи с id " + itemId));

        Item item = updateItem(oldItem, itemMapper.toItem(itemDto, userRepository.findById(userId).get()));
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDTOWithBookings getItem(Long itemId) throws NotFoundException {
        log.debug("Получен запрос на просмотр вещи с id {}", itemId);
        return itemRepository.findById(itemId)
                .map(item -> mapToItemWithBooking(item, getComments(item.getId())))
                .orElseThrow(() -> new NotFoundException("Вещь не найдена по id"));
    }

    private List<CommentDto> getComments(Long itemId) {
        return commentRepository.findAllByItemId(itemId).stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDTOWithBookings> getUserItems(Long userId) throws NotFoundException {
        log.debug("Получен запрос на просмотр вещей пользователя с id {}", userId);
        validateUserId(userId);
        List<Item> ownersItem = itemRepository.findAllByOwnerId(userId);
        return ownersItem.stream()
                .map(item -> mapToItemWithBooking(item, getComments(item.getId())))
                .collect(Collectors.toList());
    }

    private ItemDTOWithBookings mapToItemWithBooking(Item item, List<CommentDto> commentsDto) {
        List<Booking> itemBookings = bookingRepository.findAllByItemId(item.getId());

        return itemMapper.toItemDTOWithBooking(item,
                getLastBooking(itemBookings).orElse(null),
                getNextBooking(itemBookings).orElse(null),
                commentsDto);
    }

    @Override
    public Collection<ItemDto> search(String text) {
        log.debug("Получен запрос на поиск вещи по таксту {}", text);
        if (!StringUtils.hasText(text)) {
            return Collections.EMPTY_LIST;
        }
        return itemRepository.findAllByText(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) throws NotFoundException, ValidationException {
        log.debug("Получен запрос от пользователя с id {} на добавление комментария {} для вещи с id {}", userId,
                itemId, commentDto);
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Автор не найден по id " + userId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена по id " + itemId));
        validateComment(author, item);
        Comment comment = commentMapper.toComment(commentDto, author, item);
        commentRepository.save(comment);
        return commentMapper.toCommentDto(comment);
    }

    private void validateComment(User author, Item item) throws ValidationException {
        Booking booking = bookingRepository.findByBookerIdAndItemId(author.getId(), item.getId())
                .orElseThrow(() -> new ValidationException("Автор с id " + author.getId() + " не брар в аренду" +
                        "вещь с id " + item.getId()));
        if (booking.getEndDate().isAfter(LocalDateTime.now())) {
            throw new ValidationException("Срок аренды ещё не закончен");
        }
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

    private Optional<Booking> getLastBooking(List<Booking> bookings) {
        return bookings.stream()
                .filter(booking -> booking.getEndDate().isBefore(LocalDateTime.now().minus(Duration.ofMinutes(1))))
                .max(comparatorBookingEndDate);
    }

    private Optional<Booking> getNextBooking(List<Booking> bookings) {
        return bookings.stream().filter(booking -> booking.getStartDate()
                        .isAfter(LocalDateTime.now().plus(Duration.ofMinutes(1))))
                .min(comparatorBookingStartDate);
    }
}
