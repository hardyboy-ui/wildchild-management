package com.thewildchild.management.table.service.impl;

import com.thewildchild.management.common.exception.ResourceNotFoundException;
import com.thewildchild.management.table.dto.request.CreateCafeTableRequest;
import com.thewildchild.management.table.dto.request.UpdateCafeTableRequest;
import com.thewildchild.management.table.dto.response.CafeTableResponse;
import com.thewildchild.management.table.entity.CafeTable;
import com.thewildchild.management.table.repository.CafeTableRepository;
import com.thewildchild.management.table.service.mapper.CafeTableMapper;
import com.thewildchild.management.table.service.validator.CafeTableValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CafeTableServiceImplTest {

    @Mock
    private CafeTableRepository cafeTableRepository;

    @Mock
    private CafeTableMapper cafeTableMapper;

    @Mock
    private CafeTableValidator cafeTableValidator;

    @InjectMocks
    private CafeTableServiceImpl cafeTableService;


    // =========================================================
    // CREATE
    // =========================================================

    @Test
    void shouldCreateTableSuccessfully() {

        // Arrange
        CreateCafeTableRequest request =
                new CreateCafeTableRequest();

        CafeTable table =
                new CafeTable();

        CafeTable savedTable =
                new CafeTable();

        CafeTableResponse response =
                new CafeTableResponse();

        when(cafeTableMapper.toEntity(request))
                .thenReturn(table);

        when(cafeTableRepository.save(table))
                .thenReturn(savedTable);

        when(cafeTableMapper.toResponse(savedTable))
                .thenReturn(response);

        // Act
        CafeTableResponse result =
                cafeTableService.createTable(request);

        // Assert
        assertThat(result)
                .isSameAs(response);

        // Verify
        verify(cafeTableValidator)
                .validateCreate(request);

        verify(cafeTableMapper)
                .toEntity(request);

        verify(cafeTableRepository)
                .save(table);

        verify(cafeTableMapper)
                .toResponse(savedTable);
    }


    // =========================================================
    // GET BY ID
    // =========================================================

    @Test
    void shouldGetTableByIdSuccessfully() {

        // Arrange
        UUID tableId =
                UUID.randomUUID();

        CafeTable table =
                new CafeTable();

        table.setActive(true);

        CafeTableResponse response =
                new CafeTableResponse();

        when(cafeTableRepository.findById(tableId))
                .thenReturn(Optional.of(table));

        when(cafeTableMapper.toResponse(table))
                .thenReturn(response);

        // Act
        CafeTableResponse result =
                cafeTableService.getTableById(tableId);

        // Assert
        assertThat(result)
                .isSameAs(response);

        // Verify
        verify(cafeTableRepository)
                .findById(tableId);

        verify(cafeTableMapper)
                .toResponse(table);
    }


    @Test
    void shouldThrowExceptionWhenTableNotFound() {

        // Arrange
        UUID tableId =
                UUID.randomUUID();

        when(cafeTableRepository.findById(tableId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                cafeTableService.getTableById(tableId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Cafe table not found with id: " + tableId
                );

        // Verify
        verify(cafeTableRepository)
                .findById(tableId);

        verify(cafeTableMapper, never())
                .toResponse(any(CafeTable.class));
    }


    @Test
    void shouldThrowExceptionWhenGettingInactiveTable() {

        // Arrange
        UUID tableId =
                UUID.randomUUID();

        CafeTable table =
                new CafeTable();

        table.setActive(false);

        when(cafeTableRepository.findById(tableId))
                .thenReturn(Optional.of(table));

        // Act & Assert
        assertThatThrownBy(() ->
                cafeTableService.getTableById(tableId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Cafe table not found with id: " + tableId
                );

        // Verify
        verify(cafeTableRepository)
                .findById(tableId);

        verify(cafeTableMapper, never())
                .toResponse(any(CafeTable.class));
    }


    // =========================================================
    // GET ALL
    // =========================================================

    @Test
    void shouldGetAllActiveTablesSuccessfully() {

        // Arrange
        CafeTable table1 =
                new CafeTable();

        CafeTable table2 =
                new CafeTable();

        CafeTableResponse response1 =
                new CafeTableResponse();

        CafeTableResponse response2 =
                new CafeTableResponse();

        when(cafeTableRepository.findAllByActiveTrue())
                .thenReturn(List.of(table1, table2));

        when(cafeTableMapper.toResponse(table1))
                .thenReturn(response1);

        when(cafeTableMapper.toResponse(table2))
                .thenReturn(response2);

        // Act
        List<CafeTableResponse> result =
                cafeTableService.getAllTables();

        // Assert
        assertThat(result)
                .hasSize(2)
                .containsExactly(
                        response1,
                        response2
                );

        // Verify
        verify(cafeTableRepository)
                .findAllByActiveTrue();

        verify(cafeTableMapper)
                .toResponse(table1);

        verify(cafeTableMapper)
                .toResponse(table2);
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void shouldUpdateTableSuccessfully() {

        // Arrange
        UUID tableId =
                UUID.randomUUID();

        UpdateCafeTableRequest request =
                new UpdateCafeTableRequest();

        CafeTable table =
                new CafeTable();

        table.setActive(true);

        CafeTableResponse response =
                new CafeTableResponse();

        when(cafeTableRepository.findById(tableId))
                .thenReturn(Optional.of(table));

        when(cafeTableMapper.toResponse(table))
                .thenReturn(response);

        // Act
        CafeTableResponse result =
                cafeTableService.updateTable(
                        tableId,
                        request
                );

        // Assert
        assertThat(result)
                .isSameAs(response);

        // Verify
        verify(cafeTableRepository)
                .findById(tableId);

        verify(cafeTableValidator)
                .validateUpdate(
                        table,
                        request
                );

        verify(cafeTableMapper)
                .updateEntity(
                        table,
                        request
                );

        verify(cafeTableMapper)
                .toResponse(table);

        // No save() because the entity is already managed.
        verify(cafeTableRepository, never())
                .save(any(CafeTable.class));
    }


    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingTable() {

        // Arrange
        UUID tableId =
                UUID.randomUUID();

        UpdateCafeTableRequest request =
                new UpdateCafeTableRequest();

        when(cafeTableRepository.findById(tableId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                cafeTableService.updateTable(
                        tableId,
                        request
                )
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Cafe table not found with id: " + tableId
                );

        // Verify
        verify(cafeTableRepository)
                .findById(tableId);

        verify(cafeTableValidator, never())
                .validateUpdate(
                        any(CafeTable.class),
                        any(UpdateCafeTableRequest.class)
                );

        verify(cafeTableMapper, never())
                .updateEntity(
                        any(CafeTable.class),
                        any(UpdateCafeTableRequest.class)
                );
    }


    @Test
    void shouldThrowExceptionWhenUpdatingInactiveTable() {

        // Arrange
        UUID tableId =
                UUID.randomUUID();

        UpdateCafeTableRequest request =
                new UpdateCafeTableRequest();

        CafeTable table =
                new CafeTable();

        table.setActive(false);

        when(cafeTableRepository.findById(tableId))
                .thenReturn(Optional.of(table));

        // Act & Assert
        assertThatThrownBy(() ->
                cafeTableService.updateTable(
                        tableId,
                        request
                )
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Cafe table not found with id: " + tableId
                );

        // Verify
        verify(cafeTableRepository)
                .findById(tableId);

        verify(cafeTableValidator, never())
                .validateUpdate(
                        any(CafeTable.class),
                        any(UpdateCafeTableRequest.class)
                );

        verify(cafeTableMapper, never())
                .updateEntity(
                        any(CafeTable.class),
                        any(UpdateCafeTableRequest.class)
                );
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void shouldDeleteTableSuccessfully() {

        // Arrange
        UUID tableId =
                UUID.randomUUID();

        CafeTable table =
                new CafeTable();

        table.setActive(true);

        when(cafeTableRepository.findById(tableId))
                .thenReturn(Optional.of(table));

        // Act
        cafeTableService.deleteTable(tableId);

        // Assert
        assertThat(table.isActive())
                .isFalse();

        // Verify
        verify(cafeTableRepository)
                .findById(tableId);

        // Soft delete — no repository.delete()
        verify(cafeTableRepository, never())
                .delete(any(CafeTable.class));
    }


    @Test
    void shouldThrowExceptionWhenDeletingNonExistingTable() {

        // Arrange
        UUID tableId =
                UUID.randomUUID();

        when(cafeTableRepository.findById(tableId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                cafeTableService.deleteTable(tableId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Cafe table not found with id: " + tableId
                );

        // Verify
        verify(cafeTableRepository)
                .findById(tableId);

        verify(cafeTableRepository, never())
                .delete(any(CafeTable.class));
    }


    @Test
    void shouldThrowExceptionWhenDeletingInactiveTable() {

        // Arrange
        UUID tableId =
                UUID.randomUUID();

        CafeTable table =
                new CafeTable();

        table.setActive(false);

        when(cafeTableRepository.findById(tableId))
                .thenReturn(Optional.of(table));

        // Act & Assert
        assertThatThrownBy(() ->
                cafeTableService.deleteTable(tableId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Cafe table not found with id: " + tableId
                );

        // Verify
        verify(cafeTableRepository)
                .findById(tableId);

        verify(cafeTableRepository, never())
                .delete(any(CafeTable.class));
    }
}