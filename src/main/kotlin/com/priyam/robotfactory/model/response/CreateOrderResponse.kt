package com.priyam.robotfactory.model.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class CreateOrderResponse(

    @JsonProperty("order_id")
    var orderId: String? = null,

    var total: BigDecimal? = null,

    var errorMessage: String? = null
)