package com.priyam.robotfactory.repository

import com.priyam.robotfactory.constant.RobotPartCategory
import com.priyam.robotfactory.model.RobotPart
import com.priyam.robotfactory.model.Stock
import com.priyam.robotfactory.model.response.StockReservationResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.*

@Repository
class RobotPartStockRepositoryImpl : RobotPartStockRepository {

    private val logger: Logger = LoggerFactory.getLogger(RobotPartStockRepositoryImpl::class.java)
    private var robotStock: Stock = initStock()

    override fun getStock(): Stock {
        return this.robotStock
    }

    @Synchronized
    override fun reserveStock(components: List<String>): StockReservationResponse {

        logger.info("reserveStock :: request {}, stock {}", components, robotStock)
        val id = UUID.randomUUID()
        for (component in components) {
            if (robotStock.robotParts[component]!!.available == 0) {
                logger.error("reserveStock :: response :: $component is out of stock. Please try again later.")
                return StockReservationResponse(errorMessage =  "$component is out of stock. Please try again later.")
            }
        }
        updateStock(components)
        logger.info("reserveStock :: response {}, stock {}", id, robotStock)
        return StockReservationResponse(id = id.toString())
    }

    @Synchronized
    private fun updateStock(components: List<String>) {
        for (component in components) {
            val robotPart: RobotPart = robotStock.robotParts[component]!!
            robotPart.available--
        }
    }

    private fun initStock(): Stock {
        return Stock(
            mapOf(
                "A" to RobotPart(
                    price = BigDecimal.valueOf(10.28),
                    available = 9,
                    name = "Humanoid Face",
                    type = RobotPartCategory.FACE
                ),
                "B" to RobotPart(
                    price = BigDecimal.valueOf(24.07),
                    available = 7,
                    name = "LCD Face",
                    type = RobotPartCategory.FACE
                ),
                "C" to RobotPart(
                    price = BigDecimal.valueOf(13.30),
                    available = 0,
                    name = "Steampunk Face",
                    type = RobotPartCategory.FACE
                ),
                "D" to RobotPart(
                    price = BigDecimal.valueOf(28.94),
                    available = 1,
                    name = "Arms with Hands",
                    type = RobotPartCategory.ARMS
                ),
                "E" to RobotPart(
                    price = BigDecimal.valueOf(12.39),
                    available = 3,
                    name = "Arms with Grippers",
                    type = RobotPartCategory.ARMS
                ),
                "F" to RobotPart(
                    price = BigDecimal.valueOf(30.77),
                    available = 2,
                    name = "Mobility with Wheels",
                    type = RobotPartCategory.MOBILITY
                ),
                "G" to RobotPart(
                    price = BigDecimal.valueOf(55.13),
                    available = 15,
                    name = "Mobility with Legs",
                    type = RobotPartCategory.MOBILITY
                ),
                "H" to RobotPart(
                    price = BigDecimal.valueOf(50.00),
                    available = 7,
                    name = "Mobility with Tracks",
                    type = RobotPartCategory.MOBILITY
                ),
                "I" to RobotPart(
                    price = BigDecimal.valueOf(90.12),
                    available = 92,
                    name = "Material Bioplastic",
                    type = RobotPartCategory.MATERIAL
                ),
                "J" to RobotPart(
                    price = BigDecimal.valueOf(82.31),
                    available = 15,
                    name = "Material Metallic",
                    type = RobotPartCategory.MATERIAL
                )
            )
        )
    }
}