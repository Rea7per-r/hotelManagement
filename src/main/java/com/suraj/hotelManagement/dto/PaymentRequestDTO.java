package com.suraj.hotelManagement.dto;

import com.suraj.hotelManagement.model.enums.PaymentMethod;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

@Data
public class PaymentRequestDTO {

    @NotNull()
    private Long invoiceId;

    @NotNull()
    private PaymentMethod paymentMethod;

}