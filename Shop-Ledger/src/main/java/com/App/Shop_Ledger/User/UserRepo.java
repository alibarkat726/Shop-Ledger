package com.App.Shop_Ledger.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepo extends MongoRepository<Users, String> {
    List<Users> findByUsername(String username);
    boolean existsByUsernameAndRole(String username, String role);




}
