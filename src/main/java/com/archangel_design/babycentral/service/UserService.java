/*
 * Baby Central
 * Copyright (c) 2018.
 * Rafal Martinez-Marjanski
 */

package com.archangel_design.babycentral.service;

import com.archangel_design.babycentral.entity.*;
import com.archangel_design.babycentral.enums.Gender;
import com.archangel_design.babycentral.exception.InvalidArgumentException;
import com.archangel_design.babycentral.repository.UserRepository;
import com.archangel_design.babycentral.request.BabyCredentialsRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Date;

import static com.mysql.jdbc.StringUtils.isNullOrEmpty;

/**
 * Service responsible for user operations.
 */
@Service
public class UserService {

    /**
     * Used to manipulate current session.
     */
    private final SessionService sessionService;

    /**
     * Used to access persistence layer.
     */
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ShoppingCardService shoppingCardService;

    public UserService(
            final SessionService sessionService,
            final UserRepository userRepository,
            final EmailService emailService,
            final ShoppingCardService shoppingCardService
    ) {
        this.sessionService = sessionService;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.shoppingCardService = shoppingCardService;
    }

    /**
     * Compares plain text password with given hash.
     *
     * @param password plain text password
     * @param hash     hashed password
     * @return boolean
     */
    public boolean isPasswordValid(final String password, final String hash) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(password, hash);
    }

    /**
     * Returns password hash using BCrypt.
     *
     * @param passwordRaw plain text password
     * @return hashed password
     */
    public String hashPassword(final String passwordRaw) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(passwordRaw);
    }

    /**
     * Performs login, creates session or registers failed login attempt.
     *
     * @param email    user email
     * @param password user password
     * @param deviceId unique ID of the device being used
     * @return created session
     */
    public SessionEntity login(
            final String email, final String password, final String deviceId) {
        UserEntity user = userRepository.fetch(email);
        if (user == null) {
            return null;
        }

        if (isPasswordValid(password, user.getPassword())) {
            return sessionService.startSession(user, deviceId);
        }

        // @TODO: register failed login attempt
        return null;
    }

    /**
     * Performs user registration.
     *
     * @param email          valid email (to be verified)
     * @param firstName      first name or a nick name
     * @param password       valid password
     * @param passwordRepeat password repeated
     * @return newly created user entity
     * @throws InvalidArgumentException if case of validation errors
     */
    public UserEntity register(
            final String email,
            final String firstName,
            final String password,
            final String passwordRepeat
    ) throws InvalidArgumentException {
        if (isNullOrEmpty(email)
                || isNullOrEmpty(firstName)
                || isNullOrEmpty(password)
                || isNullOrEmpty(passwordRepeat)
        ) {
            throw new InvalidArgumentException("Missing required field.");
        }

        if (!password.equals(passwordRepeat)) {
            throw new InvalidArgumentException("Passwords do not match.");
        }

        UserEntity user = userRepository.userExists(email) ?
                completeUserRegistration(email, password) :
                registerNewUser(email, password);

        shoppingCardService.addSampleShoppingCardsToUser(user);

        return user;
    }

    private UserEntity completeUserRegistration(
            final String email,
            final String password
    ) {
        UserEntity user = userRepository
                .getUserWithPendingInvitation(email)
                .orElseThrow(() -> new InvalidArgumentException(
                        String.format(
                                "There is no pending invitation for user with email %s.",
                                email
                        )
                ));

        user.setLastUsage(Instant.now())
            .setPassword(hashPassword(password))
            .setRegistration(Instant.now());

        return userRepository.save(user);
    }

    private UserEntity registerNewUser(
            final String email,
            final String password
    ) {
        UserEntity user = new UserEntity();
        user.setEmail(email.toLowerCase())
            .setPassword(hashPassword(password))
            .setLastUsage(Instant.now())
            .setRegistration(Instant.now());

        return userRepository.save(user);
    }

    /**
     * @return entity of current user.
     */
    public UserEntity getCurrentUser() {
        return sessionService.getCurrentSession().getUser();
    }

    /**
     * Creates organization which allows multiple
     * users to collaborate.
     *
     * @param name organization name
     * @return updated user entity
     */
    public UserEntity createOrganization(final String name) {
        UserEntity currentUser = sessionService.getCurrentSession().getUser();

        if (currentUser.getOrganization() != null) {
            throw new InvalidArgumentException(
                    "User is already in organization "
                            + currentUser.getOrganization().getName()
            );
        }

        OrganizationEntity organizationEntity = new OrganizationEntity();
        organizationEntity.setName(name);
        currentUser.setOrganization(organizationEntity);

        return userRepository.save(currentUser);
    }

    /**
     * Updates user profile with given information (overwrite).
     *
     * @param request current user information
     * @return updated entity
     * @todo: 21/05/2018 input validation
     */
    public UserEntity updateProfile(final ProfileEntity request) {
        UserEntity userEntity = sessionService.getCurrentSession().getUser();
        userEntity.setProfile(request);

        return userRepository.save(userEntity);
    }

    public BabyEntity createBaby(
            final UserEntity user,
            final BabyCredentialsRequest babyCredentials
    ) {
        BabyEntity baby = new BabyEntity()
                .setName(StringUtils.capitalize(babyCredentials.getName()))
                .setGender(babyCredentials.getGender())
                .setBirthday(babyCredentials.getBirthday());

        user.getBabies().add(baby);
        userRepository.save(user);

        return baby;
        /*
        user.getBabies().forEach(b -> {
            if (b.getName().toLowerCase().equals(babyEntity.getName().toLowerCase()))
                throw new InvalidArgumentException(
                        "You already have a baby named " + babyEntity.getName());
        });
        */

        /*
        return updatedUser.getBabies().stream().max(Comparator.comparing(
                BabyEntity::getId)).orElseThrow(PersistenceLayerException::new);
        */
    }

    public BabyEntity getBaby(String babyId) {
        return userRepository.fetchBaby(babyId);
    }

    public BabyEntity updateBabyInformation(
            final String uuid,
            final Optional<String> name,
            final Optional<Date> birthday,
            final Optional<Gender> gender
    ) {
        BabyEntity baby = userRepository.fetchBaby(uuid);

        if (Objects.isNull(baby)) {
            // TODO Exception
        }

        name.ifPresent(s -> baby.setName(StringUtils.capitalize(s)));
        birthday.ifPresent(baby::setBirthday);
        gender.ifPresent(baby::setGender);

        return userRepository.save(baby);
    }

    public void removeBaby(final String uuid) {
        BabyEntity baby = userRepository.fetchBaby(uuid);

        if (Objects.isNull(baby))
            throw new InvalidArgumentException("Baby does not exist.");

        userRepository.delete(baby);
    }

    /**
     * Invites user to current organization,
     * returns true if successful, false otherwise
     *
     * @param email invitee's email
     * @return true on success
     */
    public Boolean inviteToOrganization(String email) {
        if (!EmailValidator.getInstance().isValid(email))
            throw new InvalidArgumentException("Invalid email.");

        UserEntity userEntity = sessionService.getCurrentSession().getUser();

        email = email.toLowerCase();

        if (email.equals(userEntity.getEmail()))
            throw new InvalidArgumentException("You cannot invite yourself silly.");

        if (userEntity.getOrganization() == null)
            throw new InvalidArgumentException("You did not create an organization yet.");

        UserEntity invitee = getOrCreate(email);

        if (invitee.getOrganization() != null)
            throw new InvalidArgumentException("User is already part of an organization.");

        invitee.setOrganization(userEntity.getOrganization());
        userRepository.save(invitee);

        return emailService.sendInvitationEmail(email, userEntity);
    }

    /**
     * Fetches user entity based on email or creates
     * new one if doesn't exist, new entity is marked
     * as created via invitation by other user
     *
     * @param email user email
     * @return created or fetched user
     */
    private UserEntity getOrCreate(String email) {
        UserEntity user = userRepository.fetch(email);

        if (user != null)
            return user;

        user = new UserEntity();
        user.setEmail(email)
            .setInvitationPending(true);

        return userRepository.save(user);
    }

    /**
     * Returns a list of members of current user's organization
     * including himself
     *
     * @return user list
     */
    public List<UserEntity> getOrganizationMembers() {
        UserEntity userEntity = sessionService.getCurrentSession().getUser();

        if (userEntity.getOrganization() == null)
            throw new InvalidArgumentException("You have no organization.");

        return userRepository.fetchOrganizationMembers(userEntity.getOrganization());
    }

    public Optional<UserEntity> getUser(String userUuid) {
        return userRepository.fetchByUuid(userUuid);
    }

    public byte[] getUserAvatarData(final String uuid) {
        UserEntity user = userRepository
                .fetchByUuid(uuid)
                .orElseThrow(() -> new InvalidArgumentException("Invalid uuid."));

        return user.getAvatar();
    }

    public UserEntity setUserAvatar(String uuid, MultipartFile file) throws IOException {
        UserEntity user = userRepository
                .fetchByUuid(uuid)
                .orElseThrow(() -> new InvalidArgumentException("Invalid uuid."));

        if (file.isEmpty())
            throw new InvalidArgumentException("Empty image.");

        user.setAvatar(file.getBytes());

        return userRepository.save(user);
    }

    public byte[] getBabyAvatarData(final String uuid) {
        BabyEntity baby = userRepository.fetchBaby(uuid);

        if (baby == null)
            throw new InvalidArgumentException("Invalid uuid.");

        return baby.getAvatar();
    }

    public BabyEntity setBabyAvatar(
            final String uuid,
            final MultipartFile file
    ) {
        BabyEntity baby = userRepository.fetchBaby(uuid);

        if (baby == null)
            throw new InvalidArgumentException("Invalid uuid.");
        if (file.isEmpty())
            throw new InvalidArgumentException("Empty image.");

        try {
            baby.setAvatar(file.getBytes());
        } catch (IOException e) {
            throw new InvalidArgumentException("Empty image.");
        }

        return userRepository.save(baby);
    }
}
