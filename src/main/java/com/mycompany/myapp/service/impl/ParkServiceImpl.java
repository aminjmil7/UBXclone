package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Park;
import com.mycompany.myapp.repository.ParkRepository;
import com.mycompany.myapp.service.ParkService;
import com.mycompany.myapp.service.dto.ParkDTO;
import com.mycompany.myapp.service.mapper.ParkMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Park}.
 */
@Service
@Transactional
public class ParkServiceImpl implements ParkService {

    private final Logger log = LoggerFactory.getLogger(ParkServiceImpl.class);

    private final ParkRepository parkRepository;

    private final ParkMapper parkMapper;

    public ParkServiceImpl(ParkRepository parkRepository, ParkMapper parkMapper) {
        this.parkRepository = parkRepository;
        this.parkMapper = parkMapper;
    }

    @Override
    public Mono<ParkDTO> save(ParkDTO parkDTO) {
        log.debug("Request to save Park : {}", parkDTO);
        return parkRepository.save(parkMapper.toEntity(parkDTO)).map(parkMapper::toDto);
    }

    @Override
    public Mono<ParkDTO> update(ParkDTO parkDTO) {
        log.debug("Request to update Park : {}", parkDTO);
        return parkRepository.save(parkMapper.toEntity(parkDTO)).map(parkMapper::toDto);
    }

    @Override
    public Mono<ParkDTO> partialUpdate(ParkDTO parkDTO) {
        log.debug("Request to partially update Park : {}", parkDTO);

        return parkRepository
            .findById(parkDTO.getId())
            .map(existingPark -> {
                parkMapper.partialUpdate(existingPark, parkDTO);

                return existingPark;
            })
            .flatMap(parkRepository::save)
            .map(parkMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ParkDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Parks");
        return parkRepository.findAllBy(pageable).map(parkMapper::toDto);
    }

    public Mono<Long> countAll() {
        return parkRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ParkDTO> findOne(Long id) {
        log.debug("Request to get Park : {}", id);
        return parkRepository.findById(id).map(parkMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Park : {}", id);
        return parkRepository.deleteById(id);
    }
}
