package ifpb.edu.br.avaliappgti.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * This class is a Data Transfer Object (DTO) that represents the JSON payload
 * for a login request. It contains the credentials needed for authentication.
 */
@Getter
@Setter
public class LoginRequest {

    private String ifRegistration;
    private String password;
}