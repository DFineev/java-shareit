package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.enums.BookingState;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UnknownBookingState;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;


import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private static final String USER_ERROR = "Пользователь не найден";
    private static final String ITEM_ERROR = "Объект не найден";
    private static final String BOOKING_ERROR = "Бронирование не найдено";
    private static final String BOOKING_STATE_ERROR = "Неизвестный статус бронирования";

    private final BookingRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public BookingInfoDto addBooking(Integer userId, BookingDto bookingDto) {
        if (!bookingValidate(bookingDto)) {
            throw new ValidateException("Валидация объекта не пройдена");
        }

        Booking booking = BookingMapper.toBooking(bookingDto);

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ObjectNotFoundException(ITEM_ERROR));

        if (!item.getAvailable()) {
            throw new ValidateException(ITEM_ERROR);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException(USER_ERROR));

        if (Objects.equals(user.getId(), item.getOwner().getId())) {
            throw new ObjectNotFoundException(USER_ERROR);
        }
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        return BookingMapper.toBookingInfoDto(repository.save(booking));
    }


    public BookingInfoDto updateBookingStatus(Integer userId, Integer bookingId, Boolean approved) {

        Booking booking = repository.findById(bookingId)
                .orElseThrow(() ->
                        new ObjectNotFoundException(BOOKING_ERROR));

        User user = userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(USER_ERROR));

        Item item = booking.getItem();

        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new ObjectNotFoundException("Операция отклонена. Пользователь не является владельцем объекта.");
        }

        BookingStatus bookingStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;

        if (booking.getStatus() == bookingStatus) {
            throw new ValidateException("Статус бронирования уже установлен " + bookingStatus);
        }

        booking.setStatus(bookingStatus);

        return BookingMapper.toBookingInfoDto(repository.save(booking));
    }


    public BookingInfoDto getCurrentBooking(Integer userId, Integer bookingId) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() ->
                        new ObjectNotFoundException(BOOKING_ERROR));

        if (!userId.equals(booking.getItem().getOwner().getId()) && !userId.equals(booking.getBooker().getId())) {
            throw new ObjectNotFoundException("This user not item owner.");
        }

        return BookingMapper.toBookingInfoDto(booking);
    }

    public List<BookingInfoDto> getBooking(Integer userId, String stateParam, Integer from, Integer size) {
        if (from < 0 || size < 0) {
            throw new ValidateException("Аргумент не может быть отрицательным");
        }

        BookingState bookingState = checkState(stateParam);

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookingList;
        LocalDateTime dateTimeNow = LocalDateTime.now();

        User user = userRepository.findById(userId).orElseThrow(()
                -> new ObjectNotFoundException(USER_ERROR));

        switch (bookingState) {
            case ALL:
                bookingList = repository.findAllByBookerIdOrderByStartDesc(user.getId(), PageRequest.of((from / size), size));
                break;
            case PAST:
                bookingList = repository.findAllByBookerIdAndEndIsBefore(user.getId(), dateTimeNow, sort, PageRequest.of((from / size), size));
                break;
            case FUTURE:
                bookingList = repository.findAllByBookerIdAndStartIsAfter(user.getId(), dateTimeNow, sort, PageRequest.of((from / size), size));
                break;
            case CURRENT:
                bookingList = repository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(user.getId(), dateTimeNow, dateTimeNow, sort, PageRequest.of((from / size), size));
                break;
            case WAITING:
                bookingList = repository.findAllByBookerIdAndStatus(user.getId(), BookingStatus.WAITING, PageRequest.of((from / size), size));
                break;
            case REJECTED:
                bookingList = repository.findAllByBookerIdAndStatus(user.getId(), BookingStatus.REJECTED, PageRequest.of((from / size), size));
                break;
            default:
                throw new UnknownBookingState(BOOKING_STATE_ERROR);
        }

        return bookingList.isEmpty() ? Collections.emptyList() : bookingList.stream()
                .map(BookingMapper::toBookingInfoDto)
                .collect(Collectors.toList());
    }

    public List<BookingInfoDto> getOwnerBooking(Integer userId, String stateParam, Integer from, Integer size) {
        if (from < 0 || size < 0) {
            throw new ValidateException("Аргумент не может быть отрицательным");
        }

        BookingState bookingState = checkState(stateParam);

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookingList;
        LocalDateTime dateTimeNow = LocalDateTime.now();

        User user = userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(USER_ERROR));

        switch (bookingState) {
            case ALL:
                bookingList = repository.findAllByItem_Owner_IdOrderByStartDesc(user.getId(), PageRequest.of((from / size), size));
                break;
            case PAST:
                bookingList = repository.findAllByItem_Owner_IdAndEndIsBefore(user.getId(), dateTimeNow, sort, PageRequest.of((from / size), size));
                break;
            case FUTURE:
                bookingList = repository.findAllByItem_Owner_IdAndStartIsAfter(user.getId(), dateTimeNow, sort, PageRequest.of((from / size), size));
                break;
            case CURRENT:
                bookingList = repository.findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(user.getId(), dateTimeNow, dateTimeNow, sort, PageRequest.of((from / size), size));
                break;
            case WAITING:
                bookingList = repository.findAllByItem_Owner_IdAndStatus(user.getId(), BookingStatus.WAITING, PageRequest.of((from / size), size));
                break;
            case REJECTED:
                bookingList = repository.findAllByItem_Owner_IdAndStatus(user.getId(), BookingStatus.REJECTED, PageRequest.of((from / size), size));
                break;
            default:
                throw new UnknownBookingState(BOOKING_STATE_ERROR);
        }

        return bookingList.isEmpty() ? Collections.emptyList() : bookingList.stream()
                .map(BookingMapper::toBookingInfoDto)
                .collect(Collectors.toList());
    }

    private BookingState checkState(String state) {
        try {
            return BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new UnknownBookingState(state);
        }
    }

    private Boolean bookingValidate(BookingDto entity) {
        return (entity.getStart() != null && entity.getEnd() != null && entity.getItemId() != null) && (!entity
                .getStart().equals(entity.getEnd())) &&
                (!entity.getEnd().isBefore(entity.getStart())) && (!entity.getStart().isBefore(LocalDateTime.now()) &&
                !entity.getEnd().isBefore(LocalDateTime.now()));
    }
}
