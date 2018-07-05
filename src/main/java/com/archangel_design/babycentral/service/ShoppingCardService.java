package com.archangel_design.babycentral.service;

import com.archangel_design.babycentral.entity.*;
import com.archangel_design.babycentral.enums.ShoppingCardStatus;
import com.archangel_design.babycentral.exception.InvalidArgumentException;
import com.archangel_design.babycentral.repository.ShoppingCardRepository;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

@Service
public class ShoppingCardService {

    private final SessionService sessionService;
    private final ShoppingCardRepository shoppingCardRepository;

    public ShoppingCardService(
            final SessionService sessionService,
            final ShoppingCardRepository shoppingCardRepository
    ) {
        this.sessionService = sessionService;
        this.shoppingCardRepository = shoppingCardRepository;
    }

    public ShoppingCardEntity createShoppingCardForCurrentUser(
            @NotNull final String name,
            final String description
    ) {
        UserEntity user = sessionService.getCurrentSession().getUser();

        return createShoppingCard(user, name, description);
    }

    public void addSampleShoppingCardsToUser(final UserEntity user) {
        ShoppingCardEntity sampleShoppingCard = createShoppingCard(
                user, "Sample", "This is sample shopping card."
        );

        ShoppingCardEntryEntity shoppingCardEntry_Milk =
                createShoppingCardEntry("Milk", 2);
        ShoppingCardEntryEntity shoppingCardEntry_Eggs =
                createShoppingCardEntry("Eggs", 10);
        ShoppingCardEntryEntity shoppingCardEntry_Gta5 =
                createShoppingCardEntry("GTA V", 1);

        sampleShoppingCard.getEntries().add(shoppingCardEntry_Milk);
        sampleShoppingCard.getEntries().add(shoppingCardEntry_Eggs);
        sampleShoppingCard.getEntries().add(shoppingCardEntry_Gta5);

        shoppingCardRepository.save(sampleShoppingCard);
    }

    private ShoppingCardEntity createShoppingCard(
            final UserEntity user,
            final String name,
            final String description
    ) {
        ShoppingCardEntity shoppingCard = new ShoppingCardEntity();

        shoppingCard
                .setUser(user)
                .setName(name)
                .setDescription(description);

        return shoppingCardRepository.save(shoppingCard);
    }

    private ShoppingCardEntryEntity createShoppingCardEntry(
            final String name,
            final int quantity
    ) {
        return new ShoppingCardEntryEntity()
                .setArticleName(name)
                .setQuantity(quantity);
    }

    public ShoppingCardEntity assignShoppingCardToUsers(
            final String uuid,
            final List<UserEntity> users
    ) {
        ShoppingCardEntity shoppingCard = shoppingCardRepository.fetch(uuid);

        if (shoppingCard == null)
            throw new InvalidArgumentException("shoppingCardEntity does not exist.");

        users.forEach(user -> shoppingCard.getAssignedUsers().add(user));

        return shoppingCardRepository.save(shoppingCard);
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
