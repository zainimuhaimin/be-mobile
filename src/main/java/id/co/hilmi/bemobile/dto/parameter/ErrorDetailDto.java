package id.co.hilmi.bemobile.dto.parameter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetailDto implements Serializable {

    private String errorCode;
    private String sourceSystem;
    private String enMessage;
    private String inMessage;

}
