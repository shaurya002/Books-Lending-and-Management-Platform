package com.shaurya.booksmanagementplatform.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record MemberRequest(

        @NotBlank(message = "First name is mandatory")
        String firstName,

        @NotBlank(message = "Last name is mandatory")
        String lastName,

        @NotBlank(message = "Email is mandatory")
        @Email(message = "Email should be valid")
        String email,

        @NotBlank(message = "Phone number is mandatory")
        @Pattern(
                regexp = "^[0-9]{10}$",
                message = "Phone number must contain exactly 10 digits"
        )
        String phone,
        String password
) {
}
