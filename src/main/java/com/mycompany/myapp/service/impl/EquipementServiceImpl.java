package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Equipement;
import com.mycompany.myapp.repository.EquipementRepository;
import com.mycompany.myapp.service.EquipementService;
import com.mycompany.myapp.service.dto.EquipementDTO;
import com.mycompany.myapp.service.mapper.EquipementMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Equipement}.
 */
@Service
@Transactional
public class EquipementServiceImpl implements EquipementService {

    private final Logger log = LoggerFactory.getLogger(EquipementServiceImpl.class);

    private final EquipementRepository equipementRepository;

    private final EquipementMapper equipementMapper;

    public EquipementServiceImpl(EquipementRepository equipementRepository, EquipementMapper equipementMapper) {
        this.equipementRepository = equipementRepository;
        this.equipementMapper = equipementMapper;
    }

    @Override
    public Mono<EquipementDTO> save(EquipementDTO equipementDTO) {
        log.debug("Request to save Equipement : {}", equipementDTO);
        return equipementRepository.save(equipementMapper.toEntity(equipementDTO)).map(equipementMapper::toDto);
    }

    @Override
    public Mono<EquipementDTO> update(EquipementDTO equipementDTO) {
        log.debug("Request to update Equipement : {}", equipementDTO);
        return equipementRepository.save(equipementMapper.toEntity(equipementDTO)).map(equipementMapper::toDto);
    }

    @Override
    public Mono<EquipementDTO> partialUpdate(EquipementDTO equipementDTO) {
        log.debug("Request to partially update Equipement : {}", equipementDTO);

        return equipementRepository
            .findById(equipementDTO.getId())
            .map(existingEquipement -> {
                equipementMapper.partialUpdate(existingEquipement, equipementDTO);

                return existingEquipement;
            })
            .flatMap(equipementRepository::save)
            .map(equipementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<EquipementDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Equipements");
        return equipementRepository.findAllBy(pageable).map(equipementMapper::toDto);
    }

    public Mono<Long> countAll() {
        return equipementRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<EquipementDTO> findOne(Long id) {
        log.debug("Request to get Equipement : {}", id);
        return equipementRepository.findById(id).map(equipementMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Equipement : {}", id);
        return equipementRepository.deleteById(id);
    }
}
