package com.dws.challenge.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class AmountTransferRequest implements Serializable {

    private static final long serialVersionUID = -6804650001482584716L;

    @NotNull(message = "From account ID cannot be null.")
    @NotEmpty(message = "From account ID cannot be empty.")
    private final String fromAccountId;

    @NotNull(message = "To account ID cannot be null.")
    @NotEmpty(message = "To account ID cannot be empty.")
    private final String toAccountId;

    @NotNull(message = "Amount cannot be null.")
    @Min(value = 0, message = "Amount must be positive.")
    private final BigDecimal amount;

    @JsonCreator
    public AmountTransferRequest(@JsonProperty("fromAccountId") String fromAccountId, @JsonProperty("toAccountId") String toAccoountId, @JsonProperty("amount") BigDecimal amount) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccoountId;
        this.amount = amount;
    }
}
