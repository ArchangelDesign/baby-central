package com.archangel_design.babycentral.service;

import com.archangel_design.babycentral.entity.*;
import com.archangel_design.babycentral.exception.InvalidArgumentException;
import com.archangel_design.babycentral.repository.ShoppingCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

@Service
public class ShoppingCardService {

    @Autowired
    SessionService sessionService;

    @Autowired
    ShoppingCardRepository shoppingCardRepository;

    public ShoppingCardEntity createShoppingCard(
            @NotNull final String name
    ) {
        UserEntity user = sessionService.getCurrentSession().getUser();

        ShoppingCardEntity shoppingCardEntity = new ShoppingCardEntity();

        shoppingCardEntity
                .setName(name)
                .setUser(user);

        return shoppingCardRepository.save(shoppingCardEntity);
    }

    public ShoppingCardEntity createEntry(
            @NotNull final String shoppingCartId,
            final String articleName,
            final Integer quantity
    ) {
        ShoppingCardEntity shoppingCardEntity = shoppingCardRepository.fetch(shoppingCartId);
        if (shoppingCardEntity == null)
            throw new InvalidArgumentException("Shopping cart does not exist. " + shoppingCartId);
        ShoppingCardEntryEntity entry = new ShoppingCardEntryEntity();
        entry.setArticleName(articleName);
        entry.setQuantity(quantity);
        shoppingCardEntity.getEntries().add(entry);

        return shoppingCardRepository.save(shoppingCardEntity);
    }

    public List<ShoppingCardEntity> getList() {
        UserEntity user = sessionService.getCurrentSession().getUser();

        return shoppingCardRepository.fetchList(user);
    }

    public ShoppingCardEntity fetch(String uuid) {
        return shoppingCardRepository.fetch(uuid);
    }

    public List<ShoppingCardEntity> getList(String uuid) {
        UserEntity user = sessionService.getCurrentSession().getUser();
        return shoppingCardRepository.fetchList(user, uuid);
    }

    public Boolean toggleIsPurchased(String uuid) {
        UserEntity user = sessionService.getCurrentSession().getUser();

        return shoppingCardRepository.toogleIsPurchased(user, uuid);
    }

}
