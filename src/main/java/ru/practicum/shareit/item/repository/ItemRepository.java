package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findById(Long id);

    @Query(" select i from Item i " +
            "where i.owner.id =  ?1")
    List<Item> findOwnersItems(long userId,Pageable pageRequest);

    @Query(" select i from Item i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))) and i.available = true")
    List<Item> findAvailableItems(String text, Pageable pageRequest);

    @Query(" select i from Item i " +
            "where i.request.id =  ?1")
    List<Item> findAllByRequestId(long id);

}