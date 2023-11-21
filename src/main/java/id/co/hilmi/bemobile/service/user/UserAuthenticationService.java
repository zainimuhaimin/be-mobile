package id.co.hilmi.bemobile.service.user;

import id.co.hilmi.bemobile.dto.user.UserDetailDto;
import id.co.hilmi.bemobile.exception.user.UserNotFoundException;
import id.co.hilmi.bemobile.exception.user.WrongPasswordException;
import id.co.hilmi.bemobile.model.user.UserAuthentication;
import id.co.hilmi.bemobile.repositories.user.UserAuthenticationRepository;
import id.co.hilmi.bemobile.repositories.user.UserProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import static id.co.hilmi.bemobile.constant.message.MessageConstant.USER_NOT_FOUND;
import static id.co.hilmi.bemobile.constant.message.MessageConstant.WRONG_PASSWORD;

@Service
@Slf4j
public class UserAuthenticationService {
    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserAuthenticationRepository userAuthenticationRepository;

    public UserDetailDto validateUser(String username, String password){
        log.info("start get user detail");
        UserDetailDto response=null;
        try {
            var userAuth = userAuthenticationRepository.getUserAuthenticationByUserName(username);

            if (ObjectUtils.isEmpty(userAuth)){
                throw new UserNotFoundException(USER_NOT_FOUND);
            }

            validatePassword(password, userAuth);


        }catch (Exception e){
            throw e;
        }
        return response;
    }

    private Boolean validatePassword(String password, UserAuthentication userAuthentication) {
        log.info("start validate password");
        Boolean isValid = true;
        String requestPassword = password;
        String existPassword = userAuthentication.getPassword();
        try {

            /*TODO : validate menggunakan encryption
            */
            if (!password.equalsIgnoreCase(existPassword)){
                /*TODO update login attemps*/
                isValid = false;
                throw new WrongPasswordException(WRONG_PASSWORD);
            }

        }catch (Exception e ){
            throw e;
        }

        return isValid;
    }
}
