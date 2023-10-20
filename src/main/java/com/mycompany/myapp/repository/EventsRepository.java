package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Events;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Events entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventsRepository extends ReactiveCrudRepository<Events, Long>, EventsRepositoryInternal {
    Flux<Events> findAllBy(Pageable pageable);

    @Query("SELECT * FROM Events entity WHERE entity.park_id = :id")
    Flux<Events> findByPark(Long id);

    @Query("SELECT * FROM Events entity WHERE entity.park_id IS NULL")
    Flux<Events> findAllWhereParkIsNull();

    @Query("SELECT * FROM Events entity WHERE entity.equipement_id = :id")
    Flux<Events> findByEquipement(Long id);

    @Query("SELECT * FROM Events entity WHERE entity.equipement_id IS NULL")
    Flux<Events> findAllWhereEquipementIsNull();

    @Override
    <S extends Events> Mono<S> save(S entity);

    @Override
    Flux<Events> findAll();

    @Override
    Mono<Events> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface EventsRepositoryInternal {
    <S extends Events> Mono<S> save(S entity);

    Flux<Events> findAllBy(Pageable pageable);

    Flux<Events> findAll();

    Mono<Events> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Events> findAllBy(Pageable pageable, Criteria criteria);

}
