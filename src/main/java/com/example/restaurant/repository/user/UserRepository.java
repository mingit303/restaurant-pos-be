package com.example.restaurant.repository.user;

import com.example.restaurant.domain.user.User;
import com.example.restaurant.domain.user.UserStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

   @Query("""
    SELECT u FROM User u
    WHERE (:keyword IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')))
    AND (:role IS NULL OR u.role.name = :role)
    AND (:statusEnum IS NULL OR u.status = :statusEnum)
    """)
     Page<User> searchUsers(@Param("keyword") String keyword,
                        @Param("role") String role,
                        @Param("statusEnum") UserStatus statusEnum,
                        Pageable pageable
    );
}
