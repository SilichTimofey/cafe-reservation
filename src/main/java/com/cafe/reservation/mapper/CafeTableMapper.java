package com.cafe.reservation.mapper;

import com.cafe.reservation.dto.TableRequestDTO;
import com.cafe.reservation.dto.TableResponseDTO;
import com.cafe.reservation.model.CafeTable;
import org.springframework.stereotype.Component;

@Component
public class CafeTableMapper {

    public CafeTable toEntity(TableRequestDTO dto) {
        return CafeTable.builder()
                .tableNumber(dto.tableNumber())
                .capacity(dto.capacity())
                .isVip(dto.isVip())
                .build();
    }

    public void update(CafeTable table, TableRequestDTO dto) {
        table.setTableNumber(dto.tableNumber());
        table.setCapacity(dto.capacity());
        table.setVip(dto.isVip());
    }

    public TableResponseDTO toResponse(CafeTable t) {
        return new TableResponseDTO(t.getId(), t.getTableNumber(), t.getCapacity(), t.isVip());
    }
}
