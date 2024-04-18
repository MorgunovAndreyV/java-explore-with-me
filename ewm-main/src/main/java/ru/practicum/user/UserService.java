package ru.practicum.user;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.exception.RecordNotFoundException;
import ru.practicum.exception.UserValidationException;
import ru.practicum.user.model.QUser;
import ru.practicum.user.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Set<User> getAll() {
        return new HashSet<>(userRepository.findAll());
    }

    public User addNew(User user) {
        validateUserInformation(user);

        User newUser = userRepository.save(user);

        log.info("Новый пользователь добавлен успешно. id:" + user.getId());

        return newUser;
    }

    public List<User> getUsersByIdsPaginated(Long[] ids, Integer from, Integer size) {
        PageRequest pageRequest;
        BooleanBuilder bd = new BooleanBuilder();
        if (ids != null) {
            bd.and(QUser.user.id.in(ids));
        }

        if (size != null && from != null) {
            validatePagination(from, size);

            pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);

            return userRepository.findAll(bd, pageRequest).getContent();
        } else {
            return StreamSupport.stream(userRepository.findAll(bd).spliterator(), false)
                    .collect(Collectors.toList());
        }

    }

    public User change(User user) throws RecordNotFoundException {
        User userFromBase = getUserById(user.getId());
        UserMapper.fillFromDto(UserMapper.toDto(user), userFromBase);

        log.info("Запись пользователя изменена успешно. id:" + user.getId());

        return userRepository.save(userFromBase);
    }

    public void delete(Long id) throws RecordNotFoundException {
        getUserById(id);
        userRepository.deleteById(id);

    }

    public User getUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            throw new RecordNotFoundException("Пользователь с id " + id + " не найден");
        }

        return user;
    }

    void validateUserInformation(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty() || user.getEmail().trim().isEmpty()) {
            throw new UserValidationException("Электронная почта не может быть пустой");
        }

        if (user.getName() == null || user.getName().isEmpty() || user.getName().trim().isEmpty()) {
            throw new UserValidationException("Имя пользователя не может быть пустым");
        } else if (user.getName().length() > 250 || user.getName().length() < 2) {
            throw new UserValidationException("Неверная длина имени пользователя");
        }

    }

    private void validatePagination(Integer numberFrom, Integer pageSize) {
        if ((pageSize != null && numberFrom != null) && (pageSize < 1 || numberFrom < 0)) {
            throw new UserValidationException("Некорректные параметры запроса с постраничным выводом");

        }

    }

}
