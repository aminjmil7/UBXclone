package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Media;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Media entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MediaRepository extends ReactiveCrudRepository<Media, Long>, MediaRepositoryInternal {
    Flux<Media> findAllBy(Pageable pageable);

    @Query("SELECT * FROM media entity WHERE entity.park_id = :id")
    Flux<Media> findByPark(Long id);

    @Query("SELECT * FROM media entity WHERE entity.park_id IS NULL")
    Flux<Media> findAllWhereParkIsNull();

    @Query("SELECT * FROM media entity WHERE entity.equipement_id = :id")
    Flux<Media> findByEquipement(Long id);

    @Query("SELECT * FROM media entity WHERE entity.equipement_id IS NULL")
    Flux<Media> findAllWhereEquipementIsNull();

    @Query("SELECT * FROM media entity WHERE entity.report_id = :id")
    Flux<Media> findByReport(Long id);

    @Query("SELECT * FROM media entity WHERE entity.report_id IS NULL")
    Flux<Media> findAllWhereReportIsNull();

    @Override
    <S extends Media> Mono<S> save(S entity);

    @Override
    Flux<Media> findAll();

    @Override
    Mono<Media> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MediaRepositoryInternal {
    <S extends Media> Mono<S> save(S entity);

    Flux<Media> findAllBy(Pageable pageable);

    Flux<Media> findAll();

    Mono<Media> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Media> findAllBy(Pageable pageable, Criteria criteria);

}
