package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Equipement;
import com.mycompany.myapp.domain.Events;
import com.mycompany.myapp.domain.Park;
import com.mycompany.myapp.service.dto.EquipementDTO;
import com.mycompany.myapp.service.dto.EventsDTO;
import com.mycompany.myapp.service.dto.ParkDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Events} and its DTO {@link EventsDTO}.
 */
@Mapper(componentModel = "spring")
public interface EventsMapper extends EntityMapper<EventsDTO, Events> {
    @Mapping(target = "park", source = "park", qualifiedByName = "parkId")
    @Mapping(target = "equipement", source = "equipement", qualifiedByName = "equipementId")
    EventsDTO toDto(Events s);

    @Named("parkId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ParkDTO toDtoParkId(Park park);

    @Named("equipementId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EquipementDTO toDtoEquipementId(Equipement equipement);
}
