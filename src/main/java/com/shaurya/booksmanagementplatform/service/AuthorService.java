package com.shaurya.booksmanagementplatform.service;

import com.shaurya.booksmanagementplatform.dto.request.AuthorRequest;
import com.shaurya.booksmanagementplatform.dto.response.AuthorResponse;
import com.shaurya.booksmanagementplatform.dto.response.PageResponse;

public interface AuthorService {

    AuthorResponse createAuthor(AuthorRequest authorRequest);

    AuthorResponse updateAuthor(Long id, AuthorRequest authorRequest);

    void deleteAuthor(Long id);

    AuthorResponse getAuthorById(Long id);

    PageResponse<AuthorResponse> findByName(String name, int page);

    PageResponse<AuthorResponse> getAllAuthors(int page);
}
