package ru.practicum.shareit.requests;


import lombok.*;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {
    private Long id;
    private String description;
    private User requester;
    private LocalDateTime created;
}
