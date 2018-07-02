package com.archangel_design.babycentral.service;

import com.archangel_design.babycentral.entity.*;
import com.archangel_design.babycentral.enums.ShoppingCardStatus;
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
    UserService userService;

    @Autowired
    ShoppingCardRepository shoppingCardRepository;

    public ShoppingCardEntity createShoppingCard(
            @NotNull final String name, final String description
    ) {
        UserEntity user = sessionService.getCurrentSession().getUser();

        ShoppingCardEntity shoppingCardEntity = new ShoppingCardEntity();

        shoppingCardEntity
                .setName(name)
                .setUser(user)
                .setDescription(description);

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

    public ShoppingCardEntity assignShoppingCard(String uuid, List<String> users) {
        ShoppingCardEntity shoppingCardEntity = shoppingCardRepository.fetch(uuid);

        if (shoppingCardEntity == null)
            throw new InvalidArgumentException("shoppingCardEntity does not exist.");

        for (String userUuid : users) {
            UserEntity userEntity = userService.getUser(userUuid);

            if (userEntity == null)
                throw new InvalidArgumentException(String.format("User %s does not exist.", userUuid));

            shoppingCardEntity.getAssignedUsers().add(userEntity);
        }

        return shoppingCardRepository.save(shoppingCardEntity);
    }

    public ShoppingCardEntity setStatusToDraft(String uuid) {
        return this.setStatus(uuid, ShoppingCardStatus.DRAFT);
    }

    public ShoppingCardEntity setStatusToPublished(String uuid) {
        return this.setStatus(uuid, ShoppingCardStatus.PUBLISHED);
    }

    public ShoppingCardEntity setStatusToFinished(String uuid) {
        return this.setStatus(uuid, ShoppingCardStatus.FINISHED);
    }

    public ShoppingCardEntity setStatus(String uuid, ShoppingCardStatus shoppingCardStatus) {
            ShoppingCardEntity shoppingCardEntity = shoppingCardRepository.fetch(uuid);

            if (shoppingCardEntity == null)
                throw new InvalidArgumentException("shoppingCardEntity does not exist.");

            shoppingCardEntity.setStatus(shoppingCardStatus);
            return shoppingCardRepository.save(shoppingCardEntity);
        }

    public void removeShoppingCardEntry(String uuid) {
        ShoppingCardEntryEntity shoppingCardEntryEntity = shoppingCardRepository.fetchEntry(uuid);

        if (shoppingCardEntryEntity == null)
            throw new InvalidArgumentException("shoppingCardEntryEntity does not exist.");

        shoppingCardRepository.delete(shoppingCardEntryEntity);
    }

    public void removeShoppingCard(String uuid) {
        ShoppingCardEntity shoppingCardEntity = shoppingCardRepository.fetch(uuid);

        if (shoppingCardEntity == null)
            throw new InvalidArgumentException("shoppingCardEntity does not exist.");

        shoppingCardRepository.delete(shoppingCardEntity);
    }

    public ShoppingCardEntity updateShoppingCard(String uuid, String name, String description) {
        ShoppingCardEntity shoppingCardEntity = shoppingCardRepository.fetch(uuid);

        if (shoppingCardEntity == null)
            throw new InvalidArgumentException("shoppingCardEntity does not exist.");

        shoppingCardEntity
                .setName(name)
                .setDescription(description);

        return shoppingCardRepository.save(shoppingCardEntity);
    }

    public ShoppingCardEntryEntity updateShoppingCardEntry(String uuid, String articleName, Integer quantity) {
        ShoppingCardEntryEntity shoppingCardEntryEntity = shoppingCardRepository.fetchEntry(uuid);

        if (shoppingCardEntryEntity == null)
            throw new InvalidArgumentException("shoppingCardEntryEntity does not exist.");

        shoppingCardEntryEntity.setArticleName(articleName);
        shoppingCardEntryEntity.setQuantity(quantity);

        return shoppingCardRepository.save(shoppingCardEntryEntity);
    }

    public ShoppingCardEntryEntity setPurchased(String uuid, Boolean purchased) {
        ShoppingCardEntryEntity shoppingCardEntryEntity = shoppingCardRepository.fetchEntry(uuid);

        if (shoppingCardEntryEntity == null)
            throw new InvalidArgumentException("shoppingCardEntryEntity does not exist.");

        shoppingCardEntryEntity.setIsPurchased(purchased);

        return shoppingCardRepository.save(shoppingCardEntryEntity);
    }
}
