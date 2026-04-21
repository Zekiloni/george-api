package com.zekiloni.george.workspace.application.usecase;

import com.zekiloni.george.platform.application.port.out.PageRepositoryPort;
import com.zekiloni.george.platform.application.usecase.PageQueryService;
import com.zekiloni.george.platform.domain.page.Page;
import com.zekiloni.george.platform.domain.page.PageStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit testovi za PageQueryService.
 */
@ExtendWith(MockitoExtension.class)
class PageQueryServiceTest {

    @Mock
    private PageRepositoryPort repository;

    private PageQueryService service;

    @BeforeEach
    void setUp() {
        service = new PageQueryService(repository);
    }

    @Test
    void findById_WithValidId_ShouldReturnPage() {
        // Given
        String pageId = "uuid-1";
        Page expectedPage = Page.builder()
            .id(pageId)
            .title("Contact Form")
            .slug("contact-form")
            .status(PageStatus.PUBLISHED)
            .build();

        when(repository.findById(pageId)).thenReturn(Optional.of(expectedPage));

        // When
        Optional<Page> result = service.findById(pageId);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Contact Form", result.get().getTitle());
        verify(repository, times(1)).findById(pageId);
    }

    @Test
    void findById_WithInvalidId_ShouldReturnEmpty() {
        // Given
        String pageId = "invalid-id";
        when(repository.findById(pageId)).thenReturn(Optional.empty());

        // When
        Optional<Page> result = service.findById(pageId);

        // Then
        assertTrue(result.isEmpty());
        verify(repository, times(1)).findById(pageId);
    }

    @Test
    void findBySlug_WithValidSlug_ShouldReturnPage() {
        // Given
        String slug = "contact-form";
        Page expectedPage = Page.builder()
            .id("uuid-1")
            .title("Contact Form")
            .slug(slug)
            .status(PageStatus.PUBLISHED)
            .build();

        when(repository.findBySlug(slug)).thenReturn(Optional.of(expectedPage));

        // When
        Optional<Page> result = service.findBySlug(slug);

        // Then
        assertTrue(result.isPresent());
        assertEquals(slug, result.get().getSlug());
        verify(repository, times(1)).findBySlug(slug);
    }

    @Test
    void findAll_ShouldReturnPagedResult() {
        // Given
        Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        Page page1 = Page.builder().id("1").title("Page 1").slug("page-1").build();
        Page page2 = Page.builder().id("2").title("Page 2").slug("page-2").build();

        org.springframework.data.domain.Page<Page> expectedPage = new PageImpl<>(
            List.of(page1, page2),
            pageable,
            2
        );

        when(repository.findAll(pageable)).thenReturn(expectedPage);

        // When
        org.springframework.data.domain.Page<Page> result = service.findAll(pageable);

        // Then
        assertEquals(2, result.getContent().size());
        assertEquals("Page 1", result.getContent().get(0).getTitle());
        verify(repository, times(1)).findAll(pageable);
    }
}

