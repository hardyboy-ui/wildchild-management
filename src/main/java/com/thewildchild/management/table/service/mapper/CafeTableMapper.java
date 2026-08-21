package com.thewildchild.management.table.service.mapper;

import com.thewildchild.management.table.dto.request.CreateCafeTableRequest;
import com.thewildchild.management.table.dto.request.UpdateCafeTableRequest;
import com.thewildchild.management.table.dto.response.CafeTableResponse;
import com.thewildchild.management.table.entity.CafeTable;
import org.springframework.stereotype.Component;

@Component
public class CafeTableMapper {

    public CafeTable toEntity(CreateCafeTableRequest request) {
        CafeTable table = new CafeTable();

        table.setTableNumber(request.getTableNumber());
        table.setCapacity(request.getCapacity());
        table.setDisplayOrder(request.getDisplayOrder());

        return table;
    }

    public void updateEntity(
            CafeTable table,
            UpdateCafeTableRequest request
    ) {
        if (request.getTableNumber() != null
                && !request.getTableNumber().isBlank()) {
            table.setTableNumber(request.getTableNumber());
        }

        if (request.getCapacity() != null) {
            table.setCapacity(request.getCapacity());
        }

        if (request.getDisplayOrder() != null) {
            table.setDisplayOrder(request.getDisplayOrder());
        }
    }

    public CafeTableResponse toResponse(CafeTable table) {
        CafeTableResponse response = new CafeTableResponse();

        response.setId(table.getId());
        response.setTableNumber(table.getTableNumber());
        response.setCapacity(table.getCapacity());
        response.setDisplayOrder(table.getDisplayOrder());

        return response;
    }
}