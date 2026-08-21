package com.thewildchild.management.table.service.validator;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.table.dto.request.CreateCafeTableRequest;
import com.thewildchild.management.table.dto.request.UpdateCafeTableRequest;
import com.thewildchild.management.table.entity.CafeTable;
import com.thewildchild.management.table.repository.CafeTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CafeTableValidator {

    private final CafeTableRepository cafeTableRepository;

    public void validateCreate(CreateCafeTableRequest request) {

        if (cafeTableRepository.existsByTableNumberIgnoreCase(
                request.getTableNumber())) {

            throw new BusinessException(
                    "Table with number '" + request.getTableNumber()
                            + "' already exists"
            );
        }
    }

    public void validateUpdate(
            CafeTable table,
            UpdateCafeTableRequest request
    ) {

        if (request.getTableNumber() != null
                && !request.getTableNumber().isBlank()
                && !request.getTableNumber()
                .equalsIgnoreCase(table.getTableNumber())
                && cafeTableRepository.existsByTableNumberIgnoreCase(
                request.getTableNumber())) {

            throw new BusinessException(
                    "Table with number '" + request.getTableNumber()
                            + "' already exists"
            );
        }
    }
}