package com.App.Shop_Ledger.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;



import java.util.Optional;

@Repository
public interface UserRepo extends MongoRepository<Users, String> {

    Optional<Users> findByUsername(String username);

    boolean existsByUsername(String username);
}
