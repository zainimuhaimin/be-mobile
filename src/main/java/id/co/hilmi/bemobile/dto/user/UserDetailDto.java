package id.co.hilmi.bemobile.dto.user;

import id.co.hilmi.bemobile.model.user.UserAuthentication;
import id.co.hilmi.bemobile.model.user.UserProfile;
import id.co.hilmi.bemobile.model.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailDto implements Serializable {
    UserProfile userProfile;
    UserAuthentication userAuthentication;
    UserRole userRole;
}
