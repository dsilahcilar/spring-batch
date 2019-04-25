package com.deniz.springbatch.model

import java.math.BigDecimal

data class Invoice(
        var name:String = "",
        var id:Long = -1,
        var date:String = "",
        var amount:BigDecimal = BigDecimal.ZERO)