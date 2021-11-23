package com.priyam.robotfactory.service

import com.priyam.robotfactory.model.request.CreateOrderRequest
import com.priyam.robotfactory.model.response.CreateOrderResponse
import org.springframework.http.HttpStatus

interface RobotFactoryService {

    fun orderRobot (request : CreateOrderRequest) : Pair<CreateOrderResponse, HttpStatus>
}