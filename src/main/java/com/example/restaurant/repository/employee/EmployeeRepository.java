package com.example.restaurant.repository.employee;

import com.example.restaurant.domain.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import com.example.restaurant.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByUserUsername(String username);
    Optional<Employee> findByUser(User user);
    @Query("""
    SELECT e FROM Employee e
    LEFT JOIN e.user u
    WHERE
    (:keyword IS NULL OR LOWER(e.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))
    AND (:gender IS NULL OR e.gender = :gender)
    AND (:role IS NULL OR u.role.name = :role)
    """)
    Page<Employee> searchEmployees(@Param("keyword") String keyword,
                                @Param("gender") String gender,
                                @Param("role") String role, 
                                Pageable pageable);

}
