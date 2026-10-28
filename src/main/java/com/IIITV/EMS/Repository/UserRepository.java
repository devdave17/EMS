
package com.IIITV.EMS.Repository;

import com.IIITV.EMS.Model.Role;
import com.IIITV.EMS.Model.User;
import com.IIITV.EMS.Model.UserStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    java.util.Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByRole(Role role);

    long countByStatus(UserStatus status);
}
