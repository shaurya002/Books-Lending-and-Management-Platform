package com.shaurya.booksmanagementplatform.serviceTests;

import com.shaurya.booksmanagementplatform.dto.request.AuthorRequest;
import com.shaurya.booksmanagementplatform.dto.response.AuthorResponse;
import com.shaurya.booksmanagementplatform.dto.response.PageResponse;
import com.shaurya.booksmanagementplatform.exception.AuthorDeletionException;
import com.shaurya.booksmanagementplatform.exception.AuthorNotFoundException;
import com.shaurya.booksmanagementplatform.exception.DuplicateAuthorException;
import com.shaurya.booksmanagementplatform.mapper.AuthorMapper;
import com.shaurya.booksmanagementplatform.model.entity.Author;
import com.shaurya.booksmanagementplatform.repositories.AuthorRepository;
import com.shaurya.booksmanagementplatform.service.impl.AuthorServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private AuthorMapper authorMapper;

    @InjectMocks
    private AuthorServiceImpl authorService;

    @Test
    void createAuthor_shouldSaveAndReturnResponse_whenRequestIsValid() {
        AuthorRequest request = new AuthorRequest("John Doe", "Bio");
        Author mapped = Author.builder().name(request.name()).biography(request.biography()).build();
        Author saved = Author.builder().id(5L).name(request.name()).biography(request.biography()).build();
        AuthorResponse expected = new AuthorResponse(5L, request.name(), request.biography());

        when(authorRepository.existsByName(request.name())).thenReturn(false);
        when(authorMapper.toEntity(request)).thenReturn(mapped);
        when(authorRepository.save(mapped)).thenReturn(saved);
        when(authorMapper.toResponse(saved)).thenReturn(expected);

        AuthorResponse actual = authorService.createAuthor(request);

        assertEquals(expected, actual);
        verify(authorRepository).existsByName(request.name());
        verify(authorMapper).toEntity(request);
        verify(authorRepository).save(mapped);
        verify(authorMapper).toResponse(saved);
    }

    @Test
    void createAuthor_shouldThrowDuplicateAuthorException_whenNameExists() {
        AuthorRequest request = new AuthorRequest("Existing", "B");
        when(authorRepository.existsByName(request.name())).thenReturn(true);

        assertThrows(DuplicateAuthorException.class, () -> authorService.createAuthor(request));

        verify(authorRepository).existsByName(request.name());
        verifyNoMoreInteractions(authorRepository, authorMapper);
    }

    @Test
    void updateAuthor_shouldUpdateAndReturnResponse_whenRequestIsValid() {
        Long id = 7L;
        AuthorRequest request = new AuthorRequest("New Name", "New Bio");
        Author existing = Author.builder().id(id).name("Old").biography("OldBio").books(Set.of()).build();
        Author updated = Author.builder().id(id).name(request.name()).biography(request.biography()).build();
        AuthorResponse expected = new AuthorResponse(id, request.name(), request.biography());

        when(authorRepository.findById(id)).thenReturn(java.util.Optional.of(existing));
        when(authorRepository.existsByName(request.name())).thenReturn(false);
        when(authorRepository.save(existing)).thenReturn(updated);
        when(authorMapper.toResponse(updated)).thenReturn(expected);

        AuthorResponse actual = authorService.updateAuthor(id, request);

        assertEquals(expected, actual);
        verify(authorRepository).findById(id);
        verify(authorRepository).save(existing);
        verify(authorMapper).toResponse(updated);
    }

    @Test
    void updateAuthor_shouldThrowAuthorNotFoundException_whenNotFound() {
        Long id = 99L;
        AuthorRequest request = new AuthorRequest("X", "Y");
        when(authorRepository.findById(id)).thenReturn(java.util.Optional.empty());

        assertThrows(AuthorNotFoundException.class, () -> authorService.updateAuthor(id, request));
        verify(authorRepository).findById(id);
    }

    @Test
    void updateAuthor_shouldThrowDuplicateAuthorException_whenNewNameAlreadyExists() {
        Long id = 8L;
        Author existing = Author.builder().id(id).name("Old").build();
        AuthorRequest request = new AuthorRequest("Taken", "B");

        when(authorRepository.findById(id)).thenReturn(java.util.Optional.of(existing));
        when(authorRepository.existsByName(request.name())).thenReturn(true);

        assertThrows(DuplicateAuthorException.class, () -> authorService.updateAuthor(id, request));

        verify(authorRepository).findById(id);
        verify(authorRepository).existsByName(request.name());
    }

    @Test
    void deleteAuthor_shouldDelete_whenNoAssociatedBooks() {
        Long id = 3L;
        Author existing = Author.builder().id(id).name("A").books(Set.of()).build();
        when(authorRepository.findById(id)).thenReturn(java.util.Optional.of(existing));

        authorService.deleteAuthor(id);

        verify(authorRepository).findById(id);
        verify(authorRepository).delete(existing);
    }

    @Test
    void deleteAuthor_shouldThrowAuthorNotFoundException_whenNotFound() {
        Long id = 100L;
        when(authorRepository.findById(id)).thenReturn(java.util.Optional.empty());

        assertThrows(AuthorNotFoundException.class, () -> authorService.deleteAuthor(id));
        verify(authorRepository).findById(id);
    }

    @Test
    void deleteAuthor_shouldThrowAuthorDeletionException_whenAuthorHasBooks() {
        Long id = 4L;
        Author existing = Author.builder().id(id).name("A").books(Set.of(new com.shaurya.booksmanagementplatform.model.entity.Book())).build();
        when(authorRepository.findById(id)).thenReturn(java.util.Optional.of(existing));

        assertThrows(AuthorDeletionException.class, () -> authorService.deleteAuthor(id));
        verify(authorRepository).findById(id);
        verifyNoMoreInteractions(authorRepository);
    }

    @Test
    void getAuthorById_shouldReturnResponse_whenFound() {
        Long id = 6L;
        Author existing = Author.builder().id(id).name("Auth").biography("B").build();
        AuthorResponse expected = new AuthorResponse(id, "Auth", "B");

        when(authorRepository.findById(id)).thenReturn(java.util.Optional.of(existing));
        when(authorMapper.toResponse(existing)).thenReturn(expected);

        AuthorResponse actual = authorService.getAuthorById(id);
        assertEquals(expected, actual);

        verify(authorRepository).findById(id);
        verify(authorMapper).toResponse(existing);
    }

    @Test
    void getAuthorById_shouldThrowAuthorNotFoundException_whenNotFound() {
        Long id = 77L;
        when(authorRepository.findById(id)).thenReturn(java.util.Optional.empty());

        assertThrows(AuthorNotFoundException.class, () -> authorService.getAuthorById(id));
        verify(authorRepository).findById(id);
        verifyNoMoreInteractions(authorMapper);
    }

    @Test
    void findByName_shouldReturnMatches_whenFound(){
        Author a1 = Author.builder().id(1L).name("John").biography("b").build();
        Author a2 = Author.builder().id(2L).name("Johnny").biography("b2").build();
        Page<Author> page = new PageImpl<>(List.of(a1,a2));
        AuthorResponse r1 = new AuthorResponse(1L, "John", "b");
        AuthorResponse r2 = new AuthorResponse(2L, "Johnny", "b2");

        when(authorRepository.findByNameContainingIgnoreCase(eq("John"), any(Pageable.class))).thenReturn(page);
        when(authorMapper.toResponse(a1)).thenReturn(r1);
        when(authorMapper.toResponse(a2)).thenReturn(r2);

        PageResponse<AuthorResponse> resp = authorService.findByName("John",0);
        assertEquals(2, resp.content().size());
        assertThat(resp.content()).containsExactly(r1, r2);

        verify(authorMapper).toResponse(a1);
        verify(authorMapper).toResponse(a2);
    }

    @Test
    void findByName_shouldReturnEmpty_whenNoMatches(){
        Page<Author> empty = new PageImpl<>(List.of());
        when(authorRepository.findByNameContainingIgnoreCase(eq("None"), any(Pageable.class))).thenReturn(empty);

        PageResponse<AuthorResponse> resp = authorService.findByName("None",0);
        assertEquals(0, resp.content().size());
    }

    @Test
    void getAllAuthors_shouldReturnPage(){
        Author a = Author.builder().id(1L).name("A").biography("b").build();
        Page<Author> page = new PageImpl<>(List.of(a));
        AuthorResponse respDto = new AuthorResponse(1L, "A", "b");

        when(authorRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(authorMapper.toResponse(a)).thenReturn(respDto);

        PageResponse<AuthorResponse> resp = authorService.getAllAuthors(0);
        assertEquals(1, resp.content().size());
        assertEquals(respDto, resp.content().get(0));
    }
}
