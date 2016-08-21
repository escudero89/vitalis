package com.rocket.vitalis.repositories;

import com.rocket.vitalis.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by sscotti on 8/13/16.
 */
public interface UserRepository extends CrudRepository<User, Long> {

    Page<User> findByName(String name, Pageable page);
    User findByEmail(String email);
}