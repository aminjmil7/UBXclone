package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Media;
import com.mycompany.myapp.repository.MediaRepository;
import com.mycompany.myapp.service.MediaService;
import com.mycompany.myapp.service.dto.MediaDTO;
import com.mycompany.myapp.service.mapper.MediaMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Media}.
 */
@Service
@Transactional
public class MediaServiceImpl implements MediaService {

    private final Logger log = LoggerFactory.getLogger(MediaServiceImpl.class);

    private final MediaRepository mediaRepository;

    private final MediaMapper mediaMapper;

    public MediaServiceImpl(MediaRepository mediaRepository, MediaMapper mediaMapper) {
        this.mediaRepository = mediaRepository;
        this.mediaMapper = mediaMapper;
    }

    @Override
    public Mono<MediaDTO> save(MediaDTO mediaDTO) {
        log.debug("Request to save Media : {}", mediaDTO);
        return mediaRepository.save(mediaMapper.toEntity(mediaDTO)).map(mediaMapper::toDto);
    }

    @Override
    public Mono<MediaDTO> update(MediaDTO mediaDTO) {
        log.debug("Request to update Media : {}", mediaDTO);
        return mediaRepository.save(mediaMapper.toEntity(mediaDTO)).map(mediaMapper::toDto);
    }

    @Override
    public Mono<MediaDTO> partialUpdate(MediaDTO mediaDTO) {
        log.debug("Request to partially update Media : {}", mediaDTO);

        return mediaRepository
            .findById(mediaDTO.getId())
            .map(existingMedia -> {
                mediaMapper.partialUpdate(existingMedia, mediaDTO);

                return existingMedia;
            })
            .flatMap(mediaRepository::save)
            .map(mediaMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MediaDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Media");
        return mediaRepository.findAllBy(pageable).map(mediaMapper::toDto);
    }

    public Mono<Long> countAll() {
        return mediaRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MediaDTO> findOne(Long id) {
        log.debug("Request to get Media : {}", id);
        return mediaRepository.findById(id).map(mediaMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Media : {}", id);
        return mediaRepository.deleteById(id);
    }
}
