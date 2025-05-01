package com.dtb.mspayment.data.repo;



import com.dtb.mspayment.data.entity.Profile;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ProfileRepository extends ReactiveCrudRepository<Profile, UUID> {
    Mono<Profile> findByUsername(String username);
    Mono<Profile> findByEmail(String email);
    Mono<Profile> findByEmailAndPasswordHash(String email, String password);
}
