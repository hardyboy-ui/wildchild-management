package com.thewildchild.management.table.service.validator;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.table.dto.request.CreateCafeTableRequest;
import com.thewildchild.management.table.dto.request.UpdateCafeTableRequest;
import com.thewildchild.management.table.entity.CafeTable;
import com.thewildchild.management.table.repository.CafeTableRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CafeTableValidatorTest {

    @Mock
    private CafeTableRepository cafeTableRepository;

    @InjectMocks
    private CafeTableValidator cafeTableValidator;


    // =========================================================
    // CREATE
    // =========================================================

    @Test
    void shouldAllowCreateWhenTableNumberDoesNotExist() {

        // Arrange
        CreateCafeTableRequest request =
                new CreateCafeTableRequest();

        request.setTableNumber("T01");

        when(cafeTableRepository.existsByTableNumberIgnoreCase("T01"))
                .thenReturn(false);

        // Act
        cafeTableValidator.validateCreate(request);

        // Verify
        verify(cafeTableRepository)
                .existsByTableNumberIgnoreCase("T01");
    }


    @Test
    void shouldThrowExceptionWhenTableNumberAlreadyExists() {

        // Arrange
        CreateCafeTableRequest request =
                new CreateCafeTableRequest();

        request.setTableNumber("T01");

        when(cafeTableRepository.existsByTableNumberIgnoreCase("T01"))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() ->
                cafeTableValidator.validateCreate(request)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Table with number 'T01' already exists"
                );

        // Verify
        verify(cafeTableRepository)
                .existsByTableNumberIgnoreCase("T01");
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void shouldAllowUpdateWhenTableNumberIsNull() {

        // Arrange
        CafeTable table =
                new CafeTable();

        table.setTableNumber("T01");

        UpdateCafeTableRequest request =
                new UpdateCafeTableRequest();

        request.setTableNumber(null);

        // Act
        cafeTableValidator.validateUpdate(
                table,
                request
        );

        // Verify
        verify(cafeTableRepository, never())
                .existsByTableNumberIgnoreCase(anyString());
    }


    @Test
    void shouldAllowUpdateWhenTableNumberIsBlank() {

        // Arrange
        CafeTable table =
                new CafeTable();

        table.setTableNumber("T01");

        UpdateCafeTableRequest request =
                new UpdateCafeTableRequest();

        request.setTableNumber("   ");

        // Act
        cafeTableValidator.validateUpdate(
                table,
                request
        );

        // Verify
        verify(cafeTableRepository, never())
                .existsByTableNumberIgnoreCase(anyString());
    }


    @Test
    void shouldAllowUpdateWhenTableNumberIsUnchanged() {

        // Arrange
        CafeTable table =
                new CafeTable();

        table.setTableNumber("T01");

        UpdateCafeTableRequest request =
                new UpdateCafeTableRequest();

        request.setTableNumber("t01");

        // Act
        cafeTableValidator.validateUpdate(
                table,
                request
        );

        // Verify
        verify(cafeTableRepository, never())
                .existsByTableNumberIgnoreCase(anyString());
    }


    @Test
    void shouldAllowUpdateWhenNewTableNumberDoesNotExist() {

        // Arrange
        CafeTable table =
                new CafeTable();

        table.setTableNumber("T01");

        UpdateCafeTableRequest request =
                new UpdateCafeTableRequest();

        request.setTableNumber("T02");

        when(cafeTableRepository.existsByTableNumberIgnoreCase("T02"))
                .thenReturn(false);

        // Act
        cafeTableValidator.validateUpdate(
                table,
                request
        );

        // Verify
        verify(cafeTableRepository)
                .existsByTableNumberIgnoreCase("T02");
    }


    @Test
    void shouldThrowExceptionWhenNewTableNumberAlreadyExists() {

        // Arrange
        CafeTable table =
                new CafeTable();

        table.setTableNumber("T01");

        UpdateCafeTableRequest request =
                new UpdateCafeTableRequest();

        request.setTableNumber("T02");

        when(cafeTableRepository.existsByTableNumberIgnoreCase("T02"))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() ->
                cafeTableValidator.validateUpdate(
                        table,
                        request
                )
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Table with number 'T02' already exists"
                );

        // Verify
        verify(cafeTableRepository)
                .existsByTableNumberIgnoreCase("T02");
    }
}