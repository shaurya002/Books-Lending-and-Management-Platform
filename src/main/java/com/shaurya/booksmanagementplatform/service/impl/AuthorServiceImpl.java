package com.shaurya.booksmanagementplatform.service.impl;

import com.shaurya.booksmanagementplatform.dto.request.AuthorRequest;
import com.shaurya.booksmanagementplatform.dto.response.AuthorResponse;
import com.shaurya.booksmanagementplatform.dto.response.PageResponse;
import com.shaurya.booksmanagementplatform.exception.AuthorDeletionException;
import com.shaurya.booksmanagementplatform.exception.AuthorNotFoundException;
import com.shaurya.booksmanagementplatform.exception.DuplicateAuthorException;
import com.shaurya.booksmanagementplatform.mapper.AuthorMapper;
import com.shaurya.booksmanagementplatform.model.entity.Author;
import com.shaurya.booksmanagementplatform.repositories.AuthorRepository;
import com.shaurya.booksmanagementplatform.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    private final int pageSize = 7; // Default page size

    @Override
    @Transactional
    public AuthorResponse createAuthor(AuthorRequest authorRequest){
        if(authorRepository.existsByName(authorRequest.name())){
            throw new DuplicateAuthorException("Author with name '" + authorRequest.name() + "' already exists.");
        }
        Author author = authorMapper.toEntity(authorRequest);
        Author savedAuthor = authorRepository.save(author);
        return authorMapper.toResponse(savedAuthor);
    }

    @Override
    @Transactional
    public AuthorResponse updateAuthor(Long id, AuthorRequest authorRequest) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() ->
                        new AuthorNotFoundException("Author not found with id: " + id));
        if (!author.getName().equals(authorRequest.name())
                && authorRepository.existsByName(authorRequest.name())) {

            throw new DuplicateAuthorException(
                    "Author with name '" + authorRequest.name() + "' already exists."
            );
        }
        author.setName(authorRequest.name());
        author.setBiography(authorRequest.biography());
        Author updatedAuthor = authorRepository.save(author);
        return authorMapper.toResponse(updatedAuthor);
    }

    @Override
    @Transactional
    public void deleteAuthor(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException("Author not found with id: " + id));

        if (!author.getBooks().isEmpty()) {
            throw new AuthorDeletionException(
                    "Cannot delete author because they are associated with existing books."
            );
        }
        authorRepository.delete(author);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorResponse getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException("Author not found with id: " + id));
        return authorMapper.toResponse(author);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AuthorResponse> findByName(String name, int page) {

        Pageable pageable =
                PageRequest.of(page, pageSize, Sort.by("name").ascending());

        Page<Author> authors =
                authorRepository.findByNameContainingIgnoreCase(name, pageable);

        Page<AuthorResponse> response =
                authors.map(authorMapper::toResponse);

        return new PageResponse<>(
                response.getContent(),
                response.getNumber(),
                response.getSize(),
                response.getTotalElements(),
                response.getTotalPages(),
                response.isFirst(),
                response.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AuthorResponse> getAllAuthors(int page) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("name").ascending());
        Page<Author> authorPage = authorRepository.findAll(pageable);
        Page<AuthorResponse> responsePage = authorPage.map(authorMapper::toResponse);
        return new PageResponse<>(
                responsePage.getContent(),
                responsePage.getNumber(),
                responsePage.getSize(),
                responsePage.getTotalElements(),
                responsePage.getTotalPages(),
                responsePage.isFirst(),
                responsePage.isLast()
        );
    }
}
