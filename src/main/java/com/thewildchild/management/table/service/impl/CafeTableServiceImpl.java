package com.thewildchild.management.table.service.impl;

import com.thewildchild.management.common.exception.ResourceNotFoundException;
import com.thewildchild.management.table.dto.request.CreateCafeTableRequest;
import com.thewildchild.management.table.dto.request.UpdateCafeTableRequest;
import com.thewildchild.management.table.dto.response.CafeTableResponse;
import com.thewildchild.management.table.entity.CafeTable;
import com.thewildchild.management.table.repository.CafeTableRepository;
import com.thewildchild.management.table.service.CafeTableService;
import com.thewildchild.management.table.service.mapper.CafeTableMapper;
import com.thewildchild.management.table.service.validator.CafeTableValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CafeTableServiceImpl implements CafeTableService {

    private final CafeTableRepository cafeTableRepository;
    private final CafeTableMapper cafeTableMapper;
    private final CafeTableValidator cafeTableValidator;

    @Override
    public CafeTableResponse createTable(
            CreateCafeTableRequest request
    ) {
        cafeTableValidator.validateCreate(request);

        CafeTable table = cafeTableMapper.toEntity(request);

        CafeTable savedTable = cafeTableRepository.save(table);

        return cafeTableMapper.toResponse(savedTable);
    }

    @Override
    @Transactional(readOnly = true)
    public CafeTableResponse getTableById(UUID tableId) {

        CafeTable table = findActiveTable(tableId);

        return cafeTableMapper.toResponse(table);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CafeTableResponse> getAllTables() {

        return cafeTableRepository.findAllByActiveTrue()
                .stream()
                .map(cafeTableMapper::toResponse)
                .toList();
    }

    @Override
    public CafeTableResponse updateTable(
            UUID tableId,
            UpdateCafeTableRequest request
    ) {
        CafeTable table = findActiveTable(tableId);

        cafeTableValidator.validateUpdate(table, request);

        cafeTableMapper.updateEntity(table, request);

        return cafeTableMapper.toResponse(table);
    }

    @Override
    public void deleteTable(UUID tableId) {

        CafeTable table = findActiveTable(tableId);

        table.setActive(false);
    }

    private CafeTable findActiveTable(UUID tableId) {

        return cafeTableRepository.findById(tableId)
                .filter(CafeTable::isActive)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cafe table not found with id: " + tableId
                        )
                );
    }
}