package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Rating;
import com.mycompany.myapp.repository.RatingRepository;
import com.mycompany.myapp.service.RatingService;
import com.mycompany.myapp.service.dto.RatingDTO;
import com.mycompany.myapp.service.mapper.RatingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Rating}.
 */
@Service
@Transactional
public class RatingServiceImpl implements RatingService {

    private final Logger log = LoggerFactory.getLogger(RatingServiceImpl.class);

    private final RatingRepository ratingRepository;

    private final RatingMapper ratingMapper;

    public RatingServiceImpl(RatingRepository ratingRepository, RatingMapper ratingMapper) {
        this.ratingRepository = ratingRepository;
        this.ratingMapper = ratingMapper;
    }

    @Override
    public Mono<RatingDTO> save(RatingDTO ratingDTO) {
        log.debug("Request to save Rating : {}", ratingDTO);
        return ratingRepository.save(ratingMapper.toEntity(ratingDTO)).map(ratingMapper::toDto);
    }

    @Override
    public Mono<RatingDTO> update(RatingDTO ratingDTO) {
        log.debug("Request to update Rating : {}", ratingDTO);
        return ratingRepository.save(ratingMapper.toEntity(ratingDTO)).map(ratingMapper::toDto);
    }

    @Override
    public Mono<RatingDTO> partialUpdate(RatingDTO ratingDTO) {
        log.debug("Request to partially update Rating : {}", ratingDTO);

        return ratingRepository
            .findById(ratingDTO.getId())
            .map(existingRating -> {
                ratingMapper.partialUpdate(existingRating, ratingDTO);

                return existingRating;
            })
            .flatMap(ratingRepository::save)
            .map(ratingMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RatingDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Ratings");
        return ratingRepository.findAllBy(pageable).map(ratingMapper::toDto);
    }

    public Mono<Long> countAll() {
        return ratingRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<RatingDTO> findOne(Long id) {
        log.debug("Request to get Rating : {}", id);
        return ratingRepository.findById(id).map(ratingMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Rating : {}", id);
        return ratingRepository.deleteById(id);
    }
}
