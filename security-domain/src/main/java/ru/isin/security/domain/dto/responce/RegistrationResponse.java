package ru.isin.security.domain.dto.responce;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RegistrationResponse extends BaseResponse {


    public RegistrationResponse(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
