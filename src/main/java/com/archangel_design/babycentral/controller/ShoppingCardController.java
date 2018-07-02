package com.archangel_design.babycentral.controller;

import com.archangel_design.babycentral.entity.ShoppingCardEntity;
import com.archangel_design.babycentral.entity.ShoppingCardEntryEntity;
import com.archangel_design.babycentral.request.CreateShoppingCardEntryRequest;
import com.archangel_design.babycentral.request.CreateShoppingCardRequest;
import com.archangel_design.babycentral.request.SetPurchasedRequest;
import com.archangel_design.babycentral.service.ShoppingCardService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shopping-card")
@Api(tags = "Shopping card management")
public class ShoppingCardController {

    @Autowired
    ShoppingCardService shoppingCardService;

    @PostMapping("/create-shopping-card")
    public ShoppingCardEntity addShoppingCard(
            @RequestBody CreateShoppingCardRequest request
    ) {
        return shoppingCardService.createShoppingCard(
                request.getName(), request.getDescription()
        );
    }

    @GetMapping("/list")
    public List<ShoppingCardEntity> getList() {
        return shoppingCardService.getList();
    }

    @PostMapping("/entry/{shoppingCardId}")
    public ShoppingCardEntity createEntry(
            @PathVariable String shoppingCardId,
            @RequestBody CreateShoppingCardEntryRequest request
    ) {
        return shoppingCardService.createEntry(
                shoppingCardId,
                request.getArticleName(),
                request.getQuantity()
        );
    }

    @GetMapping("/fetch/{uuid}")
    public ShoppingCardEntity fetchShoppingCart(
            @PathVariable String uuid
    ) {
        return shoppingCardService.fetch(uuid);
    }

    @PutMapping("/set-purchased/{uuid}")
    public ShoppingCardEntryEntity setPurchased(
            @PathVariable String uuid,
            @RequestBody SetPurchasedRequest setPurchasedRequest
    ) {
        return shoppingCardService.setPurchased(uuid, setPurchasedRequest.isPurchased());
    }

    @PostMapping("/assign/{uuid}")
    public ShoppingCardEntity assignShoppingCard(
            @PathVariable String uuid,
            @RequestBody List<String> users
    ) {
        return shoppingCardService.assignShoppingCard(uuid, users);
    }

    @GetMapping("/set-status-to-draft/{uuid}")
    public ShoppingCardEntity setStatusToDraft(
            @PathVariable String uuid
    ) {
        return shoppingCardService.setStatusToDraft(uuid);
    }

    @GetMapping("/set-status-to-published/{uuid}")
    public ShoppingCardEntity setStatusToPublished(
            @PathVariable String uuid
    ) {
        return shoppingCardService.setStatusToPublished(uuid);
    }

    @GetMapping("/set-status-to-finished/{uuid}")
    public ShoppingCardEntity setStatusToFinished(
            @PathVariable String uuid
    ) {
        return shoppingCardService.setStatusToFinished(uuid);
    }

    @DeleteMapping("/{uuid}")
    public void removeShoppingCard(
            @PathVariable String uuid
    ) {
        shoppingCardService.removeShoppingCard(uuid);
    }

    @DeleteMapping("/entry/{uuid}")
    public void removeShoppingCardEntry(
            @PathVariable String uuid
    ) {
        shoppingCardService.removeShoppingCardEntry(uuid);
    }

    @PutMapping("/{uuid}")
    public ShoppingCardEntity updateShoppingCard(
            @PathVariable("uuid") String uuid,
            @RequestBody ShoppingCardEntity shoppingCardEntity
    ) {
        return shoppingCardService.updateShoppingCard(uuid, shoppingCardEntity.getName(),
                                                        shoppingCardEntity.getDescription());
    }

    @PutMapping("/entry/{uuid}")
    public ShoppingCardEntryEntity updateShoppingCardEntry(
            @PathVariable("uuid") String uuid,
            @RequestBody ShoppingCardEntryEntity shoppingCardEntryEntity
    ) {
        return shoppingCardService.updateShoppingCardEntry(uuid, shoppingCardEntryEntity.getArticleName(),
                                                            shoppingCardEntryEntity.getQuantity());
    }

}
