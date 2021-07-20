package com.smartmarket.code.request.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.smartmarket.code.annotation.ValidDate;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class TRVDetail implements Serializable {

    private Long id;
    private Long trvId;

    @NotBlank(message = "fullName is require")
    @Size(max =255, message = "fullName should be less than or equal to 255 characters")
    private String fullName;

    @NotNull(message = "gender is require")
    private Long gender;

    @ValidDate(formatDate = "yyyy-MM-dd" ,message = "dateOfBirth is invalid date format (yyyy-MM-dd)" , blank = true)
    private String dateOfBirth;

    @NotBlank(message = "passportCard is require")
    @Size(max =63, message = "passportCard should be less than or equal to 63 characters")
    private String passportCard;


}
