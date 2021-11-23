package com.priyam.robotfactory.repository

import com.priyam.robotfactory.model.Stock
import com.priyam.robotfactory.model.response.StockReservationResponse

interface RobotPartStockRepository {

    fun getStock(): Stock

    fun reserveStock(components: List<String>): StockReservationResponse

}