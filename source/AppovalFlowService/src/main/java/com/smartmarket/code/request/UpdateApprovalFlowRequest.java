package com.smartmarket.code.request;

import com.smartmarket.code.request.entity.StepFlow;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;

@Getter
@Setter
public class UpdateApprovalFlowRequest implements Serializable {

    @NotBlank(message = "productProviderName is require")
    @Size(max = 100, message = "productProviderName should be less than or equal to 100 characters")
    private String productProviderName;

    //createProduct/updateProduct/deleteProduct/...
    @NotBlank(message = "flowName is require")
    @Size(max = 100, message = "flowName should be less than or equal to 100 characters")
    private String flowName;

    @NotNull(message = "flowStepDetail is required")
    private @Valid ArrayList<StepFlow> flowStepDetail;
}
