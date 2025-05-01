package com.dtb.msprofile.data.repo;


import com.dtb.msprofile.data.entity.Account;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;
@Repository
public interface AccountRepository extends ReactiveCrudRepository<Account, UUID> {
    Flux<Account> findByProfileId(UUID profileId);
}

