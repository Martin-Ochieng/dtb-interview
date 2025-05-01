package com.dtb.msaccount.data.repo;




import com.dtb.msaccount.data.entity.Event;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;
@Repository
public interface EventRepository extends ReactiveCrudRepository<Event, UUID> {
    Flux<Event> findByProfileId(UUID profileId);
    Flux<Event> findByTransactionId(UUID transactionId);
}

