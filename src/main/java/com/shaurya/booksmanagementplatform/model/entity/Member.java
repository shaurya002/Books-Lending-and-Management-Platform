package com.shaurya.booksmanagementplatform.model.entity;

import com.shaurya.booksmanagementplatform.model.enums.MemberStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"borrowRecords", "user"})
@ToString(exclude = {"user", "borrowRecords"})
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message = "Name is mandatory")
    @Column(nullable = false)
    private String lastName;

    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must contain exactly 10 digits")
    private String phone;

    @Column(nullable = false)
    private LocalDate membershipDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus;

    @OneToOne(mappedBy = "member")
    private User user;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BorrowRecord> borrowRecords =  new ArrayList<>();
}
