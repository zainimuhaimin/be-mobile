package id.co.hilmi.bemobile.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidateTokenResponse implements Serializable {
    private static final long serialVersionUID = 1035823295955511680L;
    private String userId;
    private String channel;
    private String tokenValue;
    private String expiresIn;
}
