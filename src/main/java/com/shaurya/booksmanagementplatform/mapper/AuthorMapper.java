package com.shaurya.booksmanagementplatform.mapper;

import com.shaurya.booksmanagementplatform.dto.request.AuthorRequest;
import com.shaurya.booksmanagementplatform.dto.response.AuthorResponse;
import com.shaurya.booksmanagementplatform.model.entity.Author;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {

    public Author toEntity(AuthorRequest authorRequest) {
        if (authorRequest == null) {
            return null;
        }
        return Author.builder()
                .name(authorRequest.name())
                .biography(authorRequest.biography())
                .build();
    }

    public AuthorResponse toResponse(Author author) {
        if (author == null) {
            return null;
        }
        return new AuthorResponse(
                author.getId(),
                author.getName(),
                author.getBiography()
        );
    }
}
