package id.co.hilmi.bemobile.service.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.hilmi.bemobile.constant.response.ConstantResponse;
import id.co.hilmi.bemobile.constant.response.GenericResponseKey;
import id.co.hilmi.bemobile.dto.auth.GenerateTokenResponseDto;
import id.co.hilmi.bemobile.dto.login.LoginRequestDto;
import id.co.hilmi.bemobile.service.token.TokenService;
import id.co.hilmi.bemobile.util.response.ResponseUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Service
@Slf4j
public class LoginService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    public ResponseEntity<Object> login(LoginRequestDto req, HttpServletRequest httpServlet){
        ResponseEntity<Object> responseEntity = null;
        OAuth2AccessToken loginSession = null;
        String username;
        String password;
        try {
            //TODO : harus di benerin throw errornya
            if (StringUtils.isEmpty(req.getUsername())){
//                throw new Exception("jangan kosong");
            }

            if (StringUtils.isEmpty(req.getPassword())){
//                throw new Exception("jangan kosong");
            }

            username = req.getUsername();
            password = req.getPassword();
            // validate username and password
            // get ke user table validate username
            // validate password

            // generate token usahakan dengan user_id
            loginSession = tokenService.getLoginSessionToken(username);
            var mapSession = objectMapper.convertValue(loginSession, GenerateTokenResponseDto.class);

            responseEntity = ResponseUtil.buildResponse(GenericResponseKey.SUCCESS.getResponseKey(), ConstantResponse.ResponseKey.KEY_SUCCESS, mapSession, HttpStatus.OK);
        }catch (Exception e){
            throw e;
        }
        return responseEntity;
    }


}
