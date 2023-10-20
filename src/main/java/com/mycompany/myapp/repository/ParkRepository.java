package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Park;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Park entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ParkRepository extends ReactiveCrudRepository<Park, Long>, ParkRepositoryInternal {
    Flux<Park> findAllBy(Pageable pageable);

    @Override
    <S extends Park> Mono<S> save(S entity);

    @Override
    Flux<Park> findAll();

    @Override
    Mono<Park> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ParkRepositoryInternal {
    <S extends Park> Mono<S> save(S entity);

    Flux<Park> findAllBy(Pageable pageable);

    Flux<Park> findAll();

    Mono<Park> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Park> findAllBy(Pageable pageable, Criteria criteria);

}
