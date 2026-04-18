package com.zekiloni.george.workspace.application.usecase;

import com.zekiloni.george.provisioning.application.usecase.ServiceAccessQueryService;
import com.zekiloni.george.provisioning.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.workspace.application.port.out.PageRepositoryPort;
import com.zekiloni.george.workspace.domain.page.Page;
import com.zekiloni.george.workspace.domain.page.PageStatus;
import com.zekiloni.george.workspace.domain.page.definition.PageDefinition;
import com.zekiloni.george.workspace.domain.page.dto.PageCreateDto;
import com.zekiloni.george.workspace.domain.page.mapper.PageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit testovi za PageCreateService.
 */
@ExtendWith(MockitoExtension.class)
class PageCreateServiceTest {

    @Mock
    private PageRepositoryPort repository;

    @Mock
    private PageMapper pageMapper;

    @Mock
    private ServiceAccessQueryService serviceAccessQueryService;

    private PageCreateService service;

    @BeforeEach
    void setUp() {
        service = new PageCreateService(repository, pageMapper, serviceAccessQueryService);
    }

    @Test
    void handle_WithValidDto_ShouldCreatePage() {
        // Given
        when(serviceAccessQueryService.hasActiveAccess(ServiceSpecification.PAGE)).thenReturn(true);
        when(repository.existsBySlug("contact-form")).thenReturn(false);

        PageCreateDto dto = PageCreateDto.builder()
            .title("Contact Form")
            .slug("contact-form")
            .description("Our contact form")
            .definition(new PageDefinition())
            .build();

        Page expectedPage = Page.builder()
            .id("uuid-1")
            .title("Contact Form")
            .slug("contact-form")
            .status(PageStatus.DRAFT)
            .version(1)
            .build();

        when(pageMapper.toEntity(dto)).thenReturn(Page.builder()
            .title("Contact Form")
            .slug("contact-form")
            .build());
        when(repository.save(any())).thenReturn(expectedPage);

        // When
        Page result = service.handle(dto);

        // Then
        assertNotNull(result);
        assertEquals("Contact Form", result.getTitle());
        assertEquals("contact-form", result.getSlug());
        assertEquals(PageStatus.DRAFT, result.getStatus());
        verify(repository, times(1)).save(any());
    }

    @Test
    void handle_WithDuplicateSlug_ShouldThrowException() {
        // Given
        when(serviceAccessQueryService.hasActiveAccess(ServiceSpecification.PAGE)).thenReturn(true);
        when(repository.existsBySlug("contact-form")).thenReturn(true);

        PageCreateDto dto = PageCreateDto.builder()
            .title("Contact Form")
            .slug("contact-form")
            .build();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> service.handle(dto));
        verify(repository, never()).save(any());
    }

    @Test
    void handle_WithoutServiceAccess_ShouldThrowException() {
        // Given
        when(serviceAccessQueryService.hasActiveAccess(ServiceSpecification.PAGE)).thenReturn(false);

        PageCreateDto dto = PageCreateDto.builder()
            .title("Contact Form")
            .slug("contact-form")
            .build();

        // When & Then
        assertThrows(IllegalStateException.class, () -> service.handle(dto));
        verify(repository, never()).save(any());
    }
}

