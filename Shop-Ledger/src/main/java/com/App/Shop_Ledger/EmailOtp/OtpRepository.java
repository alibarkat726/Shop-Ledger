package com.App.Shop_Ledger.EmailOtp;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends MongoRepository<OtpEntry, String> {

    void deleteByEmail(String email);

    Optional<OtpEntry> findByEmail(String email);

}
