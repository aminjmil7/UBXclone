package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Park;
import com.mycompany.myapp.service.dto.ParkDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Park} and its DTO {@link ParkDTO}.
 */
@Mapper(componentModel = "spring")
public interface ParkMapper extends EntityMapper<ParkDTO, Park> {}
