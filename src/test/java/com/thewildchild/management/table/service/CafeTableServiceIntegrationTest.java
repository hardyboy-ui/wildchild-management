package com.thewildchild.management.table.service;

import com.thewildchild.management.table.dto.request.CreateCafeTableRequest;
import com.thewildchild.management.table.dto.request.UpdateCafeTableRequest;
import com.thewildchild.management.table.dto.response.CafeTableResponse;
import com.thewildchild.management.table.repository.CafeTableRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CafeTableServiceIntegrationTest {

    @Autowired
    private CafeTableService cafeTableService;

    @Autowired
    private CafeTableRepository cafeTableRepository;


    @Test
    void shouldCreateTable() {

        CreateCafeTableRequest request =
                new CreateCafeTableRequest();

        request.setTableNumber("T-01");
        request.setCapacity(4);
        request.setDisplayOrder(1);

        CafeTableResponse response =
                cafeTableService.createTable(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getTableNumber())
                .isEqualTo("T-01");
        assertThat(response.getCapacity())
                .isEqualTo(4);
        assertThat(response.getDisplayOrder())
                .isEqualTo(1);


        assertThat(
                cafeTableRepository.existsById(response.getId())
        ).isTrue();
    }


    @Test
    void shouldGetTableById() {

        CreateCafeTableRequest request =
                new CreateCafeTableRequest();

        request.setTableNumber("T-02");
        request.setCapacity(2);
        request.setDisplayOrder(2);

        CafeTableResponse createdTable =
                cafeTableService.createTable(request);

        CafeTableResponse response =
                cafeTableService.getTableById(
                        createdTable.getId()
                );

        assertThat(response).isNotNull();
        assertThat(response.getId())
                .isEqualTo(createdTable.getId());
        assertThat(response.getTableNumber())
                .isEqualTo("T-02");
        assertThat(response.getCapacity())
                .isEqualTo(2);

    }


    @Test
    void shouldGetAllActiveTables() {

        CreateCafeTableRequest firstRequest =
                createRequest(
                        "T-03",
                        4,
                        3
                );

        CreateCafeTableRequest secondRequest =
                createRequest(
                        "T-04",
                        6,
                        4
                );

        cafeTableService.createTable(firstRequest);
        cafeTableService.createTable(secondRequest);

        List<CafeTableResponse> tables =
                cafeTableService.getAllTables();

        assertThat(tables)
                .hasSize(2);

        assertThat(tables)
                .extracting(CafeTableResponse::getTableNumber)
                .containsExactlyInAnyOrder(
                        "T-03",
                        "T-04"
                );
    }


    @Test
    void shouldUpdateTable() {

        CreateCafeTableRequest createRequest =
                createRequest(
                        "T-05",
                        4,
                        5
                );

        CafeTableResponse createdTable =
                cafeTableService.createTable(createRequest);

        UpdateCafeTableRequest updateRequest =
                new UpdateCafeTableRequest();

        updateRequest.setTableNumber("T-05-UPDATED");
        updateRequest.setCapacity(8);
        updateRequest.setDisplayOrder(10);

        CafeTableResponse updatedTable =
                cafeTableService.updateTable(
                        createdTable.getId(),
                        updateRequest
                );

        assertThat(updatedTable.getId())
                .isEqualTo(createdTable.getId());

        assertThat(updatedTable.getTableNumber())
                .isEqualTo("T-05-UPDATED");

        assertThat(updatedTable.getCapacity())
                .isEqualTo(8);

        assertThat(updatedTable.getDisplayOrder())
                .isEqualTo(10);


        var savedTable =
                cafeTableRepository.findById(
                        createdTable.getId()
                ).orElseThrow();

        assertThat(savedTable.getTableNumber())
                .isEqualTo("T-05-UPDATED");

        assertThat(savedTable.getCapacity())
                .isEqualTo(8);

        assertThat(savedTable.getDisplayOrder())
                .isEqualTo(10);
    }


    @Test
    void shouldSoftDeleteTable() {

        CreateCafeTableRequest request =
                createRequest(
                        "T-06",
                        4,
                        6
                );

        CafeTableResponse createdTable =
                cafeTableService.createTable(request);

        UUID tableId = createdTable.getId();

        assertThat(
                cafeTableRepository.findById(tableId)
        ).isPresent();

        cafeTableService.deleteTable(tableId);

        var deletedTable =
                cafeTableRepository.findById(tableId)
                        .orElseThrow();

        assertThat(deletedTable.isActive())
                .isFalse();
    }


    private CreateCafeTableRequest createRequest(
            String tableNumber,
            Integer capacity,
            Integer displayOrder
    ) {

        CreateCafeTableRequest request =
                new CreateCafeTableRequest();

        request.setTableNumber(tableNumber);
        request.setCapacity(capacity);
        request.setDisplayOrder(displayOrder);

        return request;
    }
}