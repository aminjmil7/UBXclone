package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Equipement;
import com.mycompany.myapp.domain.Park;
import com.mycompany.myapp.service.dto.EquipementDTO;
import com.mycompany.myapp.service.dto.ParkDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Equipement} and its DTO {@link EquipementDTO}.
 */
@Mapper(componentModel = "spring")
public interface EquipementMapper extends EntityMapper<EquipementDTO, Equipement> {
    @Mapping(target = "park", source = "park", qualifiedByName = "parkId")
    EquipementDTO toDto(Equipement s);

    @Named("parkId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ParkDTO toDtoParkId(Park park);
}
