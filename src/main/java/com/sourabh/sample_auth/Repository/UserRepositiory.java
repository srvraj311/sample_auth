package com.sourabh.sample_auth.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sourabh.sample_auth.Entity.User;

import java.util.Optional;

@Repository
public interface UserRepositiory extends CrudRepository<User, Integer> {

    @Query("SELECT u FROM User u WHERE u.username = :username")
    public Optional<User> findOneByUserName(@Param("username") String username);
}
