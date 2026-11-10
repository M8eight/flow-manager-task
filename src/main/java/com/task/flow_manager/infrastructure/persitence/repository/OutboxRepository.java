package com.task.flow_manager.infrastructure.persitence.repository;

import com.task.flow_manager.infrastructure.persitence.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxRepository extends JpaRepository<Outbox, UUID> {

    @Query(value = """
        select * from outbox
        where status = 'PENDING'
            and next_attempt_at <= now()
        order by next_attempt_at
        limit :batch
    """, nativeQuery = true)
    List<Outbox> findBatch(@Param("batch") int batch);

    @Modifying
    @Query(value = """
        update outbox
        set status = 'SUCCESS',
            published_at = now()
        where id = :eventId
            and status = 'PENDING'
    """, nativeQuery = true)
    int setSuccess(UUID eventId);

    @Modifying
    @Query(value = """
        update outbox
        set status = 'PENDING',
            next_attempt_at = :nextAttemptAt,
            last_error = :errorMessage,
            retry_attempts = retry_attempts + 1
        where id = :id
            and status = 'PENDING'
    """, nativeQuery = true)
    void setPending(@Param("id") UUID id,
                    @Param("nextAttemptAt") Instant nextAttemptAt,
                    @Param("errorMessage") String errorMessage);

    @Modifying
    @Query(value = """
        update outbox
        set status = 'FAILED',
            next_attempt_at = null,
            last_error = :errorMessage
        where id = :id
            and status = 'PENDING'
    """, nativeQuery = true)
    void setFailed(@Param("id") UUID id,
                   @Param("errorMessage") String errorMessage);

    @Modifying
    @Query(value = """
        with batch_to_delete as (
            select o.id
            from outbox o
            where o.created_at < :threshold
                and o.status = 'PENDING'
            order by o.created_at
            limit :batchSize
        )
        delete from outbox
            where id in (select id from batch_to_delete)
    """, nativeQuery = true)
    int deletePending(@Param("threshold") Instant threshold, @Param("batchSize") int batchSize);

    @Modifying
    @Query(value = """
        with batch_to_delete as (
            select o.id
            from outbox o
            where o.created_at < :threshold
                and o.status in ('SUCCESS', 'FAILED')
            order by o.created_at
            limit :batchSize
        )
        delete from outbox
            where id in (select id from batch_to_delete)
    """, nativeQuery = true)
    int deleteFinal(@Param("threshold") Instant threshold, @Param("batchSize") int batchSize);

}
