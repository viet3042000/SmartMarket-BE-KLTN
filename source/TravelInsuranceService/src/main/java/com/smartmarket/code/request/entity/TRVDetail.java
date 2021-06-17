package com.smartmarket.code.request.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
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

    @NotEmpty(message = "fullName is require")
    private String fullName;

    @NotNull(message = "gender is require")
    private Long gender;

    @NotEmpty(message = "dateofBirth is require")
    private String dateofBirth;

    @NotEmpty(message = "passportCard is require")
    private String passportCard;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTrvId() {
        return trvId;
    }

    public void setTrvId(Long trvId) {
        this.trvId = trvId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getGender() {
        return gender;
    }

    public void setGender(Long gender) {
        this.gender = gender;
    }

    public String getDateofBirth() {
        return dateofBirth;
    }

    public void setDateofBirth(String dateofBirth) {
        this.dateofBirth = dateofBirth;
    }

    public String getPassportCard() {
        return passportCard;
    }

    public void setPassportCard(String passportCard) {
        this.passportCard = passportCard;
    }
}
