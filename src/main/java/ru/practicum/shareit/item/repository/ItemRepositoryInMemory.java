package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryInMemory implements ItemRepository {
    private final Map<Long, Item> itemMap = new HashMap<>();
    private  long itemId = 0;

    @Override
    public Item create(Item item) {
        item.setId(++itemId);
        itemMap.put(itemId,item);
        return item;
    }

    @Override
    public void update(long itemId, Item newItem) {
         itemMap.put(itemId,newItem);
    }

    @Override
    public Optional<Item> getItemById(long id) {
        if (itemMap.containsKey(id)) {
            return Optional.of(itemMap.get(id));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Item> findAllByIdUser(long userId) {
       List<Item> itemList = itemMap.values().stream()
               .filter(item -> item.getOwner().getId() == userId).collect(Collectors.toList());
       return itemList;
    }

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(itemMap.values());
    }
}
