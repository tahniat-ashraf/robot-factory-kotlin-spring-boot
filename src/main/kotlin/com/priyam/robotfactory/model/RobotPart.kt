package com.priyam.robotfactory.model

import com.priyam.robotfactory.constant.RobotPartCategory
import java.math.BigDecimal

data class RobotPart(
    val price: BigDecimal,
    var available: Int,
    val name: String,
    val type: RobotPartCategory
)