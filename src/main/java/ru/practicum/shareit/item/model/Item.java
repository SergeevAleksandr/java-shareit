package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.model.User;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest request;
}
