/*
 * Baby Central
 * Copyright (c) 2018.
 * Rafal Martinez-Marjanski
 */

package com.archangel_design.babycentral.controller;

import com.archangel_design.babycentral.entity.BabyEntity;
import com.archangel_design.babycentral.entity.ProfileEntity;
import com.archangel_design.babycentral.entity.UserEntity;
import com.archangel_design.babycentral.request.BabyCredentialsRequest;
import com.archangel_design.babycentral.request.EmailRequest;
import com.archangel_design.babycentral.request.LocationUpdateRequest;
import com.archangel_design.babycentral.service.LocationService;
import com.archangel_design.babycentral.service.SessionService;
import com.archangel_design.babycentral.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
    public List<BabyEntity> getBabiesForCurrentUser() {
        return userService.getBabiesForCurrentUser();
    }

    @PostMapping("/baby")
    @ApiOperation("Create a baby entity")
    public BabyEntity createBaby(
            @RequestBody BabyCredentialsRequest babyCredentials
    ) {
        return userService.createBaby(
                sessionService.getCurrentSession().getUser(),
                babyCredentials
        );
    }

    @GetMapping("/baby/{babyId}")
    public BabyEntity getBaby(
            @PathVariable String babyId
    ) {
        return userService.getBaby(babyId);
    }

    @PutMapping("/baby/{uuid}")
    public BabyEntity updateBabyInformation(
            @PathVariable final String uuid,
            @RequestBody final BabyCredentialsRequest babyCredentials
    ) {
        return userService.updateBabyInformation(
                uuid,
                Optional.ofNullable(babyCredentials.getName()),
                Optional.ofNullable(babyCredentials.getBirthday()),
                Optional.ofNullable(babyCredentials.getGender())
        );
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
    public ResponseEntity<byte[]> getUserAvatar(
            @PathVariable final String uuid
    ) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        byte[] userAvatarData = userService.getUserAvatarData(uuid);

        return new ResponseEntity<>(userAvatarData, headers, HttpStatus.OK);
    }

    @PostMapping("/avatar/{uuid}")
    public UserEntity setUserAvatar(
            @PathVariable final String uuid,
            @RequestParam("file") final MultipartFile file
    ) throws IOException {
        return userService.setUserAvatar(uuid, file);
    }

    @GetMapping("baby/avatar/{uuid}")
    public ResponseEntity<byte[]> getBabyAvatar(
            @PathVariable final String uuid
    ) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        byte[] babyAvatarData = userService.getBabyAvatarData(uuid);

        return new ResponseEntity<>(babyAvatarData, headers, HttpStatus.OK);
    }

    @PostMapping("baby/avatar/{uuid}")
    public BabyEntity setBabyAvatar(
            @PathVariable final String uuid,
            @RequestParam("file") final MultipartFile file
    ) {
        return userService.setBabyAvatar(uuid, file);
    }
}
