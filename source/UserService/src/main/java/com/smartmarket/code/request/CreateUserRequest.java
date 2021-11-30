package com.smartmarket.code.request;

import com.smartmarket.code.annotation.ValidDate;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.io.Serializable;

@Getter
@Setter
public class CreateUserRequest implements Serializable {

    @NotBlank(message = "userName is require")
    @Size(max = 30, message = "userName should be less than or equal to 30 characters")
    private String userName;

    @NotBlank(message = "password is require")
    @Size(max = 30, message = "password should be less than or equal to 30 characters")
    private String password;

    @NotBlank(message = "email is require")
    @Size(max =50, message = "email should be less than or equal to 50 characters")
    @Email
    private String email;

    @Size(max = 15, message = "phoneNumber should be less than or equal to 15 characters")
    @Pattern(regexp="(^$|[0-9]{9,12})")
    private String phoneNumber;

    @Size(max = 255, message = "address should be less than or equal to 255 characters")
    private String address;

//    @NotNull(message = "gender is require")
    private Integer gender;

    private String identifyNumber;

    @ValidDate(formatDate = "yyyy-MM-dd" ,message = "birthDate is invalid date format (yyyy-MM-dd)" , blank = true)
    private String birthDate;

    @Size(max = 100, message = "fullName should be less than or equal to 100 characters")
    private String fullName;

    private Integer enabled;

    private String provider;

    @NotNull(message = "role is required")
    private String role ;

}
