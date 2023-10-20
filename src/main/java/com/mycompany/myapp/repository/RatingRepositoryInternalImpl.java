package com.mycompany.myapp.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.mycompany.myapp.domain.Rating;
import com.mycompany.myapp.repository.rowmapper.EquipementRowMapper;
import com.mycompany.myapp.repository.rowmapper.ParkRowMapper;
import com.mycompany.myapp.repository.rowmapper.RatingRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Rating entity.
 */
@SuppressWarnings("unused")
class RatingRepositoryInternalImpl extends SimpleR2dbcRepository<Rating, Long> implements RatingRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ParkRowMapper parkMapper;
    private final EquipementRowMapper equipementMapper;
    private final RatingRowMapper ratingMapper;

    private static final Table entityTable = Table.aliased("rating", EntityManager.ENTITY_ALIAS);
    private static final Table parkTable = Table.aliased("park", "park");
    private static final Table equipementTable = Table.aliased("equipement", "equipement");

    public RatingRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ParkRowMapper parkMapper,
        EquipementRowMapper equipementMapper,
        RatingRowMapper ratingMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Rating.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.parkMapper = parkMapper;
        this.equipementMapper = equipementMapper;
        this.ratingMapper = ratingMapper;
    }

    @Override
    public Flux<Rating> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Rating> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = RatingSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ParkSqlHelper.getColumns(parkTable, "park"));
        columns.addAll(EquipementSqlHelper.getColumns(equipementTable, "equipement"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(parkTable)
            .on(Column.create("park_id", entityTable))
            .equals(Column.create("id", parkTable))
            .leftOuterJoin(equipementTable)
            .on(Column.create("equipement_id", entityTable))
            .equals(Column.create("id", equipementTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Rating.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Rating> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Rating> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Rating process(Row row, RowMetadata metadata) {
        Rating entity = ratingMapper.apply(row, "e");
        entity.setPark(parkMapper.apply(row, "park"));
        entity.setEquipement(equipementMapper.apply(row, "equipement"));
        return entity;
    }

    @Override
    public <S extends Rating> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
