package id.co.hilmi.bemobile.repositories.user;

import id.co.hilmi.bemobile.dto.user.UserDetailInterfaceDto;
import id.co.hilmi.bemobile.model.user.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

}
