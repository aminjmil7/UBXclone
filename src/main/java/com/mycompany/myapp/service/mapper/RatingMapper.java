package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Equipement;
import com.mycompany.myapp.domain.Park;
import com.mycompany.myapp.domain.Rating;
import com.mycompany.myapp.service.dto.EquipementDTO;
import com.mycompany.myapp.service.dto.ParkDTO;
import com.mycompany.myapp.service.dto.RatingDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Rating} and its DTO {@link RatingDTO}.
 */
@Mapper(componentModel = "spring")
public interface RatingMapper extends EntityMapper<RatingDTO, Rating> {
    @Mapping(target = "park", source = "park", qualifiedByName = "parkId")
    @Mapping(target = "equipement", source = "equipement", qualifiedByName = "equipementId")
    RatingDTO toDto(Rating s);

    @Named("parkId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ParkDTO toDtoParkId(Park park);

    @Named("equipementId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EquipementDTO toDtoEquipementId(Equipement equipement);
}
