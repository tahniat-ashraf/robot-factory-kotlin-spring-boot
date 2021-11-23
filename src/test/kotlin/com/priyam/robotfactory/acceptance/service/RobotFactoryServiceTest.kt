package com.priyam.robotfactory.acceptance.service

import com.priyam.robotfactory.constant.RobotPartCategory
import com.priyam.robotfactory.model.RobotPart
import com.priyam.robotfactory.model.Stock
import com.priyam.robotfactory.model.request.CreateOrderRequest
import com.priyam.robotfactory.model.response.StockReservationResponse
import com.priyam.robotfactory.repository.RobotPartStockRepository
import com.priyam.robotfactory.service.RobotFactoryService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.math.BigDecimal
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest
class RobotFactoryServiceTest {

    @MockBean
    private lateinit var repository: RobotPartStockRepository

    @Autowired
    private lateinit var service: RobotFactoryService

    private var robotStock: Stock = initialize()


    private final fun initialize(): Stock {
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

    @Test
    fun `should order a robot`() {

        val id = UUID.randomUUID().toString()

        Mockito.`when`(repository.getStock()).thenReturn(robotStock)
        Mockito.`when`(repository.reserveStock(listOf("I", "A", "D", "F")))
            .thenReturn(StockReservationResponse(id = id))

        val orderRobotResponse = service.orderRobot(CreateOrderRequest(components = listOf("I", "A", "D", "F")))

        assert(orderRobotResponse.first.total!!.compareTo(BigDecimal.valueOf(160.11))==0)
        assert(orderRobotResponse.first.orderId == id)
        assert(orderRobotResponse.second.is2xxSuccessful)

    }

    @Test
    fun `error case - part out of stock`() {

        Mockito.`when`(repository.getStock()).thenReturn(robotStock)
        Mockito.`when`(repository.reserveStock(listOf("I", "C", "D", "F")))
            .thenReturn(StockReservationResponse(errorMessage = "C is out of stock. Please try again later."))

        val orderRobotResponse = service.orderRobot(CreateOrderRequest(components = listOf("I", "C", "D", "F")))

        assert(orderRobotResponse.first.errorMessage.equals("C is out of stock. Please try again later."))
        assert(orderRobotResponse.second.is4xxClientError)

    }

    @Test
    fun `error case - invalid request`() {

        //won't be reached
        Mockito.`when`(repository.getStock()).thenThrow(RuntimeException())
        Mockito.`when`(repository.reserveStock(listOf("E", "A", "G")))
            .thenThrow(RuntimeException())

        val orderRobotResponse = service.orderRobot(CreateOrderRequest(components = listOf("E", "A", "G")))

        assert(orderRobotResponse.second.is4xxClientError)

    }

    @Test
    fun `error case - invalid request - 2 part from same category`() {

        Mockito.`when`(repository.getStock()).thenReturn(robotStock)
        //won't be reached
        Mockito.`when`(repository.reserveStock(listOf("E", "A", "G", "H")))
            .thenThrow(RuntimeException())

        val orderRobotResponse = service.orderRobot(CreateOrderRequest(components = listOf("E", "A", "G", "H")))

        assert(orderRobotResponse.second.is4xxClientError)

    }

    @Test
    fun `error case - invalid request - invalid component`() {

        Mockito.`when`(repository.getStock()).thenReturn(robotStock)
        //won't be reached
        Mockito.`when`(repository.reserveStock(listOf("E", "A", "G", "Z")))
            .thenThrow(RuntimeException())

        val orderRobotResponse = service.orderRobot(CreateOrderRequest(components = listOf("E", "A", "G", "Z")))

        assert(orderRobotResponse.second.is4xxClientError)

    }

    @Test
    fun `error case - invalid request - empty list`() {

        Mockito.`when`(repository.getStock()).thenReturn(robotStock)

        val orderRobotResponse = service.orderRobot(CreateOrderRequest(components = listOf()))

        assert(orderRobotResponse.second.is4xxClientError)

    }

}