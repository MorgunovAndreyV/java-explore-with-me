package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("/admin/users")
    public List<UserDto> getByIds(@Nullable @RequestParam(required = false) Long[] ids,
                                  @Nullable @RequestParam(defaultValue = "0", required = false) Integer from,
                                  @Nullable @RequestParam(defaultValue = "10", required = false) Integer size) {

        return userService.getUsersByIdsPaginated(ids, from, size)
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/admin/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addNew(@RequestBody UserDto user) {
        return UserMapper.toDto(userService.addNew(UserMapper.toEntity(user)));
    }

    @DeleteMapping("/admin/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }

}
