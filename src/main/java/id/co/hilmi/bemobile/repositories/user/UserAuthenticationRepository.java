package id.co.hilmi.bemobile.repositories.user;

import id.co.hilmi.bemobile.model.user.UserAuthentication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuthenticationRepository extends JpaRepository<UserAuthentication, Long> {
    UserAuthentication getUserAuthenticationByUserName(String username);
}
