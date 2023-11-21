package id.co.hilmi.bemobile.dto.user;

import id.co.hilmi.bemobile.model.user.UserAuthentication;
import id.co.hilmi.bemobile.model.user.UserProfile;
import id.co.hilmi.bemobile.model.user.UserRole;

public interface UserDetailInterfaceDto {
    UserProfile getUserProfile();
    UserAuthentication getUserAuthentication();
    UserRole getUserRole();
}
