package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

@Entity
@Table(name = "item")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "available")
    private Boolean available;
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private User owner;
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = ItemRequest.class)
    @JoinColumn(name = "request", referencedColumnName = "id")
    private ItemRequest request;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        Item otherItem = (Item) o;
        return id != null && id.equals(otherItem.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
