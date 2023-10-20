package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Equipement;
import com.mycompany.myapp.domain.Media;
import com.mycompany.myapp.domain.Park;
import com.mycompany.myapp.domain.Report;
import com.mycompany.myapp.service.dto.EquipementDTO;
import com.mycompany.myapp.service.dto.MediaDTO;
import com.mycompany.myapp.service.dto.ParkDTO;
import com.mycompany.myapp.service.dto.ReportDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Media} and its DTO {@link MediaDTO}.
 */
@Mapper(componentModel = "spring")
public interface MediaMapper extends EntityMapper<MediaDTO, Media> {
    @Mapping(target = "park", source = "park", qualifiedByName = "parkId")
    @Mapping(target = "equipement", source = "equipement", qualifiedByName = "equipementId")
    @Mapping(target = "report", source = "report", qualifiedByName = "reportId")
    MediaDTO toDto(Media s);

    @Named("parkId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ParkDTO toDtoParkId(Park park);

    @Named("equipementId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EquipementDTO toDtoEquipementId(Equipement equipement);

    @Named("reportId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ReportDTO toDtoReportId(Report report);
}
