package com.smartmarket.code.request.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.smartmarket.code.annotation.ValidDate;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class TRVDetail implements Serializable {

    private Long id;
    private Long trvId;

    @NotNull(message = "fullName is require")
    private String fullName;

    @NotNull(message = "gender is require")
    private Long gender;

    @ValidDate(formatDate = "dd/MM/yyyy" ,message = "dateOfBirth is invalid date format (dd/MM/yyyy)" , blank = true)
    private String dateOfBirth;

    @NotNull(message = "passportCard is require")
    private String passportCard;


}
