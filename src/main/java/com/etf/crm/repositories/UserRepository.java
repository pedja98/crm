package com.etf.crm.repositories;

import com.etf.crm.dtos.AuthUserDto;
import com.etf.crm.dtos.UserDto;
import com.etf.crm.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameAndDeletedFalse(String username);
    Optional<User> findByEmailAndDeletedFalse(String email);

    @Query("SELECT new com.etf.crm.dtos.UserDto(u.id, u.firstName, u.lastName, u.email, u.username, " +
            "u.phone, u.type, u.language, s.id, s.name, u.salesmen," +
            " cb.id, cb.username, mb.id, mb.username, u.dateCreated, u.dateModified)" +
            "FROM User u " +
            "LEFT JOIN u.shop s " +
            "LEFT JOIN u.createdBy cb " +
            "LEFT JOIN u.modifiedBy mb " +
            "WHERE u.username = :username AND u.deleted = false")
    Optional<UserDto> findUserDtoByUsernameAndDeletedFalse(String username);

    @Query("SELECT new com.etf.crm.dtos.AuthUserDto(u.username, u.type, u.language, u.password)" +
            "FROM User u " +
            "WHERE u.username = :username AND u.deleted = false")
    Optional<AuthUserDto> findAuthUserDtoByUsernameAndDeletedFalse(String username);

    @Query("SELECT new com.etf.crm.dtos.UserDto(u.id, u.firstName, u.lastName, u.email, u.username, " +
            "u.phone, u.type, u.language, s.id, s.name, u.salesmen," +
            " cb.id, cb.username, mb.id, mb.username, u.dateCreated, u.dateModified)" +
            "FROM User u " +
            "LEFT JOIN u.shop s " +
            "LEFT JOIN u.createdBy cb " +
            "LEFT JOIN u.modifiedBy mb " +
            "WHERE u.deleted = false")
    Optional<List<UserDto>> findAllUserDtoByDeletedFalse();
}
