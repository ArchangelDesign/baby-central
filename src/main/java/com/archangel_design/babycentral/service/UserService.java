/*
 * Baby Central
 * Copyright (c) 2018.
 * Rafal Martinez-Marjanski
 */

package com.archangel_design.babycentral.service;

import com.archangel_design.babycentral.entity.BabyEntity;
import com.archangel_design.babycentral.entity.OrganizationEntity;
import com.archangel_design.babycentral.entity.SessionEntity;
import com.archangel_design.babycentral.entity.UserEntity;
import com.archangel_design.babycentral.entity.ProfileEntity;
import com.archangel_design.babycentral.exception.InvalidArgumentException;
import com.archangel_design.babycentral.exception.PersistenceLayerException;
import com.archangel_design.babycentral.exception.UnreachableResourceException;
import com.archangel_design.babycentral.repository.UserRepository;
import org.apache.commons.io.IOUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.mysql.jdbc.StringUtils.isNullOrEmpty;

/**
 * Service responsible for user operations.
 */
@Service
public class UserService {

    /**
     * Used to manipulate current session.
     */
    @Autowired
    private SessionService sessionService;

    /**
     * Used to access persistence layer.
     */
    @Autowired
    private UserRepository userRepository;

    @Autowired
    EmailService emailService;

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
            final String email, final String firstName,
            final String password, final String passwordRepeat
    ) throws InvalidArgumentException {
        if (isNullOrEmpty(email) || isNullOrEmpty(firstName)
                || isNullOrEmpty(password) || isNullOrEmpty(passwordRepeat)) {
            throw new InvalidArgumentException("Missing required field.");
        }

        if (!password.equals(passwordRepeat)) {
            throw new InvalidArgumentException("Passwords do not match.");
        }

        if (userRepository.userExists(email)) {
            // check if user has been invited
            UserEntity userEntity = userRepository.getUserWithPendingInvitation(email);
            if (userEntity != null) {
                userEntity
                        .setLastUsage(new Date())
                        .setPassword(hashPassword(password))
                        .setRegistration(new Date());

                return userRepository.save(userEntity);
            }
            throw new InvalidArgumentException(
                    String.format("User %s is already registered.", email)
            );
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email.toLowerCase())
                .setLastUsage(new Date())
                .setPassword(hashPassword(password))
                .setRegistration(new Date());

        return userRepository.save(userEntity);
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

    public BabyEntity createBaby(final UserEntity user, final BabyEntity babyEntity) {
        if (babyEntity.getId() != null)
            throw new InvalidArgumentException("Baby ID provided.");

        babyEntity.setName(
                babyEntity.getName().substring(0, 1).toUpperCase()
                        + babyEntity.getName().substring(1)
        );

        user.getBabies().forEach(b -> {
            if (b.getName().toLowerCase().equals(babyEntity.getName().toLowerCase()))
                throw new InvalidArgumentException(
                        "You already have a baby named " + babyEntity.getName());
        });

        user.getBabies().add(babyEntity);
        UserEntity updatedUser = userRepository.save(user);
        return updatedUser.getBabies().stream().max(Comparator.comparing(
                BabyEntity::getId)).orElseThrow(PersistenceLayerException::new);
    }

    public BabyEntity getBaby(String babyId) {
        return userRepository.fetchBaby(babyId);
    }

    public BabyEntity updateBabyInformation(BabyEntity babyEntity) {
        if (babyEntity.getUuid() == null)
            throw new InvalidArgumentException("No baby ID provided");

        return null;
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

    public UserEntity getUser(String userUuid) {
        return userRepository.fetchByUuid(userUuid);
    }

    public ResponseEntity getUserAvatar(String uuid) {
        UserEntity user = userRepository.fetchByUuid(uuid);

        if (user == null)
            throw new InvalidArgumentException("Invalid uuid.");

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<>(user.getAvatar(), headers, HttpStatus.OK);
    }

    public UserEntity setUserAvatar(String uuid, MultipartFile file) throws IOException {
        UserEntity user = userRepository.fetchByUuid(uuid);

        if (file.isEmpty())
            throw new InvalidArgumentException("Empty image.");
        if (user == null)
            throw new InvalidArgumentException("Invalid uuid.");

        user.setAvatar(file.getBytes());

        return userRepository.save(user);
    }

    public ResponseEntity<byte[]> getBabyAvatar(String uuid) {
        BabyEntity baby = userRepository.fetchBaby(uuid);

        if (baby == null)
            throw new InvalidArgumentException("Invalid uuid.");

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<byte[]>(baby.getAvatar(), headers,
                HttpStatus.OK);
    }

    public BabyEntity setBabyAvatar(String uuid, MultipartFile file) throws IOException {
        BabyEntity baby = userRepository.fetchBaby(uuid);

        if (file.isEmpty())
            throw new InvalidArgumentException("Empty image.");
        if (baby == null)
            throw new InvalidArgumentException("Invalid uuid.");

        baby.setAvatar(file.getBytes());

        return userRepository.save(baby);
    }
}
