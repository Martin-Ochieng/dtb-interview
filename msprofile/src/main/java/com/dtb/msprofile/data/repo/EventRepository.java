package com.dtb.msprofile.data.repo;




import com.dtb.msprofile.data.entity.Event;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;
@Repository
public interface EventRepository extends ReactiveCrudRepository<Event, UUID> {
    Flux<Event> findByProfileId(UUID profileId);
    Flux<Event> findByTransactionId(UUID transactionId);
}

