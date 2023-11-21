package id.co.hilmi.bemobile.controller.login;

import id.co.hilmi.bemobile.dto.login.LoginRequestDto;
import id.co.hilmi.bemobile.service.login.LoginService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @ApiOperation("Login")
    @PostMapping("/login")
    ResponseEntity<Object> login(@RequestBody LoginRequestDto request, HttpServletRequest httpServletRequest){
        ResponseEntity<Object> responseEntity = null;
        try {
            responseEntity = loginService.login(request, httpServletRequest);
        }catch (Exception e){
            throw e;
        }
        return responseEntity;
    }
}
