package com.smartmarket.code.request;

import com.smartmarket.code.model.User;
import com.smartmarket.code.request.entity.Role;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.SequenceGenerator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CreateUserRequest implements Serializable {

    private User user;
    private ArrayList<Long> roles ;

}
