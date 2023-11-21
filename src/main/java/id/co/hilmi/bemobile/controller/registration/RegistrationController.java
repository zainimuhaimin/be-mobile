package id.co.hilmi.bemobile.controller.registration;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/registration")
public class RegistrationController {

    @PostMapping("/test")
    public String test(){
        return "sadds";
    }
}
