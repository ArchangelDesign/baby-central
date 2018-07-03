/*
 * Baby Central
 * Copyright (c) 2018.
 * Rafal Martinez-Marjanski
 */

package com.archangel_design.babycentral.controller;

import com.archangel_design.babycentral.entity.BabyEntity;
import com.archangel_design.babycentral.entity.ProfileEntity;
import com.archangel_design.babycentral.entity.UserEntity;
import com.archangel_design.babycentral.exception.UnreachableResourceException;
import com.archangel_design.babycentral.request.EmailRequest;
import com.archangel_design.babycentral.request.LocationUpdateRequest;
import com.archangel_design.babycentral.service.LocationService;
import com.archangel_design.babycentral.service.SessionService;
import com.archangel_design.babycentral.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Main controller responsible for user operations
 */
@RestController
@RequestMapping("/user")
@Api(tags = "User operations")
public class UserController {

    private final UserService userService;
    private final LocationService locationService;
    private final SessionService sessionService;

    public UserController(
            final UserService userService,
            final LocationService locationService,
            final SessionService sessionService
    ) {
        this.userService = userService;
        this.locationService = locationService;
        this.sessionService = sessionService;
    }

    @GetMapping("/my-account")
    public UserEntity myAccount() {
        return userService.getCurrentUser();
    }

    @PostMapping("/update-profile")
    public UserEntity updateProfile(
            @RequestBody ProfileEntity request
    ) {
        return userService.updateProfile(request);
    }

    @PostMapping("/create-organization/{name}")
    public UserEntity createOrganization(
            @PathVariable String name
    ) {
        return userService.createOrganization(name);
    }

    @PostMapping("/update-location")
    public void reportLocation(
            @RequestBody LocationUpdateRequest request
    ) {
        locationService.reportPosition(
                request.getLat(),
                request.getLon(),
                request.getAlt(),
                request.getPrecision(),
                request.getDate(),
                sessionService.getCurrentSession().getUser(),
                request.getDeviceId()
        );
    }

    @GetMapping("/babies")
    public List<BabyEntity> getBabies() {
        return sessionService.getCurrentSession().getUser().getBabies();
    }

    @PostMapping("/baby")
    @ApiOperation("Create a baby entity")
    public BabyEntity createBaby(
            @RequestBody BabyEntity babyEntity
    ) {
        return userService.createBaby(
                sessionService.getCurrentSession().getUser(),
                babyEntity);
    }

    @GetMapping("/baby/{babyId}")
    public BabyEntity getBaby(
            @PathVariable String babyId
    ) {
        return userService.getBaby(babyId);
    }

    @PutMapping("/baby")
    public BabyEntity updateBabyInformation(
            @RequestBody BabyEntity babyEntity
    ) {
        return userService.updateBabyInformation(babyEntity);
    }

    @DeleteMapping("/baby/{uuid}")
    public void removeBaby(@PathVariable final String uuid) {
        userService.removeBaby(uuid);
    }

    @PostMapping("/invite")
    public Boolean inviteUser(@RequestBody final EmailRequest email) {
        return userService.inviteToOrganization(email.getEmail());
    }

    @GetMapping("/organization")
    public List<UserEntity> getOrganizationMembers() {
        return userService.getOrganizationMembers();
    }

    @GetMapping("/avatar/{uuid}")
    public ResponseEntity<byte[]> getUserAvatar(@PathVariable final String uuid) {
        return userService.getUserAvatar(uuid);
    }

    @PostMapping("/avatar/{uuid}")
    public UserEntity setUserAvatar(
            @PathVariable final String uuid,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        return userService.setUserAvatar(uuid, file);
    }

    @GetMapping("baby/avatar/{uuid}")
    public ResponseEntity<byte[]> getBabyAvatar(@PathVariable final String uuid) {
        return userService.getBabyAvatar(uuid);
    }

    @PostMapping("baby/avatar/{uuid}")
    public BabyEntity setBabyAvatar(
            @PathVariable final String uuid,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        return userService.setBabyAvatar(uuid, file);
    }
}
