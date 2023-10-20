package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Equipement;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Equipement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EquipementRepository extends ReactiveCrudRepository<Equipement, Long>, EquipementRepositoryInternal {
    Flux<Equipement> findAllBy(Pageable pageable);

    @Query("SELECT * FROM equipement entity WHERE entity.park_id = :id")
    Flux<Equipement> findByPark(Long id);

    @Query("SELECT * FROM equipement entity WHERE entity.park_id IS NULL")
    Flux<Equipement> findAllWhereParkIsNull();

    @Override
    <S extends Equipement> Mono<S> save(S entity);

    @Override
    Flux<Equipement> findAll();

    @Override
    Mono<Equipement> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface EquipementRepositoryInternal {
    <S extends Equipement> Mono<S> save(S entity);

    Flux<Equipement> findAllBy(Pageable pageable);

    Flux<Equipement> findAll();

    Mono<Equipement> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Equipement> findAllBy(Pageable pageable, Criteria criteria);

}
