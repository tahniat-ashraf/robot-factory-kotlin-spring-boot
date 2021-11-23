package com.priyam.robotfactory.service

import com.priyam.robotfactory.constant.Error
import com.priyam.robotfactory.constant.RobotPartCategory
import com.priyam.robotfactory.model.RobotPart
import com.priyam.robotfactory.model.Stock
import com.priyam.robotfactory.model.request.CreateOrderRequest
import com.priyam.robotfactory.model.response.CreateOrderResponse
import com.priyam.robotfactory.model.response.StockReservationResponse
import com.priyam.robotfactory.repository.RobotPartStockRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class RobotFactoryServiceImpl(
    val robotPartStockRepository: RobotPartStockRepository
) : RobotFactoryService {

    override fun orderRobot(request: CreateOrderRequest): Pair<CreateOrderResponse, HttpStatus> {

        val createOrderResponse = CreateOrderResponse()
        if (isRequestValid(request, createOrderResponse)) return Pair(
            createOrderResponse,
            HttpStatus.UNPROCESSABLE_ENTITY
        )

        val stock = getStock()
        var sum = BigDecimal.ZERO
        val robotTypeCountMap: MutableMap<RobotPartCategory, Int> = mutableMapOf()

        for (component in request.components) {
            if (isComponentInvalid(stock, component, createOrderResponse)) return Pair(
                createOrderResponse,
                HttpStatus.UNPROCESSABLE_ENTITY
            )

            val robotPart: RobotPart = stock.robotParts[component]!!
            sum = sum.add(robotPart.price)
            robotTypeCountMap[robotPart.type] = robotTypeCountMap.getOrDefault(robotPart.type, 0) + 1
        }

        if (isPartSelectionInvalid(robotTypeCountMap, createOrderResponse)) return Pair(
            createOrderResponse,
            HttpStatus.UNPROCESSABLE_ENTITY
        )

        return reserveStock(request, createOrderResponse, sum)
    }


    private fun reserveStock(
        request: CreateOrderRequest,
        createOrderResponse: CreateOrderResponse,
        sum: BigDecimal
    ): Pair<CreateOrderResponse, HttpStatus> {
        val reservationResponse: StockReservationResponse = robotPartStockRepository.reserveStock(request.components)

        return if (reservationResponse.errorMessage != null) {
            createOrderResponse.errorMessage = reservationResponse.errorMessage
            Pair(createOrderResponse, HttpStatus.UNPROCESSABLE_ENTITY)
        } else {
            createOrderResponse.orderId = reservationResponse.id
            createOrderResponse.total = sum
            Pair(createOrderResponse, HttpStatus.CREATED)
        }

    }

    private fun isRequestValid(
        request: CreateOrderRequest,
        createOrderResponse: CreateOrderResponse
    ): Boolean {
        if (request.components.size != 4) {
            createOrderResponse.errorMessage = Error.INVALID_REQUEST.errorMessage
            return true
        }
        return false
    }

    private fun isPartSelectionInvalid(
        robotTypeCountMap: MutableMap<RobotPartCategory, Int>,
        createOrderResponse: CreateOrderResponse
    ): Boolean {
        for (entry in robotTypeCountMap) {
            if (entry.value != 1) {
                createOrderResponse.errorMessage = Error.INVALID_REQUEST.errorMessage
                return true
            }
        }
        return false
    }


    private fun isComponentInvalid(
        stock: Stock,
        component: String,
        createOrderResponse: CreateOrderResponse
    ): Boolean {
        if (!stock.robotParts.containsKey(component)) {
            createOrderResponse.errorMessage = "Invalid component $component"
            return true
        }
        return false
    }

    private fun getStock(): Stock = robotPartStockRepository.getStock()
}