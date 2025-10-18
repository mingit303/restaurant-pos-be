    package com.example.restaurant.domain.employee;

    import com.example.restaurant.domain.user.User;
    import jakarta.persistence.*;
    import lombok.*;
    import java.time.LocalDate;

    @Entity
    @Table(name = "employees")
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    @ToString(exclude = "user")
    public class Employee {

        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String fullName;

        private String gender;
        private LocalDate birthDate;
        private String citizenId;
        private String email;
        private String phone;
        private String position;

        @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
        @JoinColumn(name = "user_id", unique = true)
        private User user;
    }
