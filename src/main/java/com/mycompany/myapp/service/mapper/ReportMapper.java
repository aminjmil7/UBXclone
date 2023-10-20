package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Equipement;
import com.mycompany.myapp.domain.Park;
import com.mycompany.myapp.domain.Report;
import com.mycompany.myapp.service.dto.EquipementDTO;
import com.mycompany.myapp.service.dto.ParkDTO;
import com.mycompany.myapp.service.dto.ReportDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Report} and its DTO {@link ReportDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReportMapper extends EntityMapper<ReportDTO, Report> {
    @Mapping(target = "equipement", source = "equipement", qualifiedByName = "equipementId")
    @Mapping(target = "park", source = "park", qualifiedByName = "parkId")
    ReportDTO toDto(Report s);

    @Named("equipementId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EquipementDTO toDtoEquipementId(Equipement equipement);

    @Named("parkId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ParkDTO toDtoParkId(Park park);
}
