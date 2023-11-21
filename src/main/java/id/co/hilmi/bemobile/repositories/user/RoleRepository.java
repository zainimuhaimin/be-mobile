package id.co.hilmi.bemobile.repositories.user;

import id.co.hilmi.bemobile.model.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
