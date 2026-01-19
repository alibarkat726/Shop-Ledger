package com.App.Shop_Ledger.Repository;

import com.App.Shop_Ledger.model.InviteToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface InviteTokenRepo extends MongoRepository<InviteToken, String> {

    Optional<InviteToken> findByToken(String token);

    void deleteByUserId(String userId);
}

