package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Events;
import com.mycompany.myapp.repository.EventsRepository;
import com.mycompany.myapp.service.EventsService;
import com.mycompany.myapp.service.dto.EventsDTO;
import com.mycompany.myapp.service.mapper.EventsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Events}.
 */
@Service
@Transactional
public class EventsServiceImpl implements EventsService {

    private final Logger log = LoggerFactory.getLogger(EventsServiceImpl.class);

    private final EventsRepository eventsRepository;

    private final EventsMapper eventsMapper;

    public EventsServiceImpl(EventsRepository eventsRepository, EventsMapper eventsMapper) {
        this.eventsRepository = eventsRepository;
        this.eventsMapper = eventsMapper;
    }

    @Override
    public Mono<EventsDTO> save(EventsDTO eventsDTO) {
        log.debug("Request to save Events : {}", eventsDTO);
        return eventsRepository.save(eventsMapper.toEntity(eventsDTO)).map(eventsMapper::toDto);
    }

    @Override
    public Mono<EventsDTO> update(EventsDTO eventsDTO) {
        log.debug("Request to update Events : {}", eventsDTO);
        return eventsRepository.save(eventsMapper.toEntity(eventsDTO)).map(eventsMapper::toDto);
    }

    @Override
    public Mono<EventsDTO> partialUpdate(EventsDTO eventsDTO) {
        log.debug("Request to partially update Events : {}", eventsDTO);

        return eventsRepository
            .findById(eventsDTO.getId())
            .map(existingEvents -> {
                eventsMapper.partialUpdate(existingEvents, eventsDTO);

                return existingEvents;
            })
            .flatMap(eventsRepository::save)
            .map(eventsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<EventsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Events");
        return eventsRepository.findAllBy(pageable).map(eventsMapper::toDto);
    }

    public Mono<Long> countAll() {
        return eventsRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<EventsDTO> findOne(Long id) {
        log.debug("Request to get Events : {}", id);
        return eventsRepository.findById(id).map(eventsMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Events : {}", id);
        return eventsRepository.deleteById(id);
    }
}
