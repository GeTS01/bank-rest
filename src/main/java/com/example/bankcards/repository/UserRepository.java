package com.example.bankcards.repository;

import com.example.bankcards.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("""
            select u from User u
            where :search is null
               or lower(u.username) like lower(concat('%', :search, '%'))
               or lower(u.fullName) like lower(concat('%', :search, '%'))
            """)
    Page<User> search(@Param("search") String search, Pageable pageable);
}
