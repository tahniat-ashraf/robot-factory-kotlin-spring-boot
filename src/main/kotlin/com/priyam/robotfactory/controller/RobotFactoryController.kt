package com.priyam.robotfactory.controller

import com.priyam.robotfactory.model.request.CreateOrderRequest
import com.priyam.robotfactory.model.response.CreateOrderResponse
import com.priyam.robotfactory.service.RobotFactoryService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class RobotFactoryController(
    private val robotFactoryService: RobotFactoryService
) {

    private val logger: Logger = LoggerFactory.getLogger(RobotFactoryController::class.java)


    @PostMapping(path = ["/orders"])
    fun createOrder(@RequestBody request: CreateOrderRequest): ResponseEntity<CreateOrderResponse> {

        logger.info("createOrder :: request {}", request)
        val robotOrderResponse = robotFactoryService.orderRobot(request)
        logger.info("createOrder :: response {}", robotOrderResponse)
        return ResponseEntity(robotOrderResponse.first, robotOrderResponse.second)

    }

}