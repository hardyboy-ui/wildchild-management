package com.thewildchild.management.table.service;

import com.thewildchild.management.table.dto.request.CreateCafeTableRequest;
import com.thewildchild.management.table.dto.request.UpdateCafeTableRequest;
import com.thewildchild.management.table.dto.response.CafeTableResponse;

import java.util.List;
import java.util.UUID;

public interface CafeTableService {

    CafeTableResponse createTable(CreateCafeTableRequest request);

    CafeTableResponse getTableById(UUID tableId);

    List<CafeTableResponse> getAllTables();

    CafeTableResponse updateTable(
            UUID tableId,
            UpdateCafeTableRequest request
    );

    void deleteTable(UUID tableId);
}