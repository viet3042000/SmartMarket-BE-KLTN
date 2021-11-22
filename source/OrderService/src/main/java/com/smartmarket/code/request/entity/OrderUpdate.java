package com.smartmarket.code.request.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.smartmarket.code.annotation.ValidDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class OrderUpdate {

    //order id of product
    @NotBlank(message = "private orderId; is require")
    @Size(max = 50, message = "orderId should be less than or equal to 50 characters")
    private String orderId;

    @NotBlank(message = "orderReference is require")
    @Size(max = 50, message = "orderReference should be less than or equal to 50 characters")
    private String orderReference;

    @NotBlank(message = "ordBillFirstName is require")
    @Size(max = 255, message = "ordBillFirstName should be less than or equal to 255 characters")
//    @JsonDeserialize(using = StringOnlyDeserializer.class)
    private String ordBillFirstName;

    @NotBlank(message = "ordBillMobile is require")
    @Size(max = 50, message = "ordBillMobile should be less than or equal to 50 characters")
    @Pattern(regexp="(^$|[0-9]{9,12})")
    private String ordBillMobile;

    @NotNull(message = "ordBillStreet1 is require")
    @Size(max = 255, message = "ordBillStreet1 should be less than or equal to 255 characters")
    private String ordBillStreet1;

    @NotBlank(message = "ordBillEmail is require")
    @Size(max =250, message = "ordBillEmail should be less than or equal to 250 characters")
    @Email
    private String ordBillEmail;

    @Range(min= 1, max= 2)
    @NotNull(message = "ordStatus is require")
    private Long ordStatus;

}
