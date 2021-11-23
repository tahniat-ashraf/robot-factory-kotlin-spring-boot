package com.priyam.robotfactory.acceptance

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderARobotAcceptanceTest {

    @LocalServerPort
    val springBootPort: Int = 0

    @Test
    fun `should order a robot`() {
        postOrder(
            """
                    { 
                        "components": ["I","A","D","F"]
                    }
                """
        ).then()
            .assertThat()
            .statusCode(HttpStatus.CREATED.value())
            .body("order_id", notNullValue())
            .body("total", equalTo(160.11f))
    }

    /*
    * Constraint Violation : component D out of stock as result of previous successful order
    * */
    @Test
    fun `should give out of stock error`() {
        postOrder(
            """
                    { 
                        "components": ["B","G","D","J"]
                    }
                """
        ).then()
            .assertThat()
            .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
            .body("errorMessage", equalTo("D is out of stock. Please try again later."))
    }


    @Test
    fun `should order a robot2`() {
        postOrder(
            """
                    { 
                        "components": ["B","G","E","J"]
                    }
                """
        ).then()
            .assertThat()
            .statusCode(HttpStatus.CREATED.value())
            .body("order_id", notNullValue())
            .body("total", equalTo(173.9f))
    }

    @Test
    fun `should not allow invalid robot configuration`() {
        postOrder(
            """
                    {
                        "components": ["A", "C", "I", "D"]
                    }
                """
        ).then()
            .assertThat()
            .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
    }

    @Test
    fun `should not allow invalid body`() {
        postOrder(
            """
                    { 
                        "components": "BENDER"
                    }
                """
        ).then()
            .assertThat()
            .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    /*
    * Constraint Violation : component C out of stock
    * */
    @Test
    fun `should give out of stock error2`() {
        postOrder(
            """
                    { 
                        "components": ["I","C","D","F"]
                    }
                """
        ).then()
            .assertThat()
            .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
            .body("errorMessage", equalTo("C is out of stock. Please try again later."))
    }


    /*
    * Constraint Violation : component list size < 4
    * */
    @Test
    fun `should give invalid request error`() {
        postOrder(
            """
                    { 
                        "components": ["I","C","D"]
                    }
                """
        ).then()
            .assertThat()
            .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
            .body("errorMessage", startsWith("Invalid request"))
    }

    /*
    * Constraint Violation : Not all the unique part category ordered
    * */
    @Test
    fun `should give invalid request error2`() {
        postOrder(
            """
                    { 
                        "components": ["I","A","B","F"]
                    }
                """
        ).then()
            .assertThat()
            .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
            .body("errorMessage", startsWith("Invalid request"))
    }

    /*
    * Constraint Violation : component list empty
    * */
    @Test
    fun `should give invalid request error3`() {
        postOrder(
            """
                    { 
                        "components": []
                    }
                """
        ).then()
            .assertThat()
            .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
            .body("errorMessage", startsWith("Invalid request"))
    }

    /*
    * Constraint Violation : invalid component in request
    * */
    @Test
    fun `should give invalid request error5`() {
        postOrder(
            """
                    { 
                        "components": ["I","A","D","Z"]
                    }
                """
        ).then()
            .assertThat()
            .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
            .body("errorMessage", equalTo("Invalid component Z"))
    }

    /*
  * Constraint Violation : component list size > 4
  * */
    @Test
    fun `should give invalid request error6`() {
        postOrder(
            """
                    { 
                        "components": ["I","A","D","F","F"]
                    }
                """
        ).then()
            .assertThat()
            .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
            .body("errorMessage", startsWith("Invalid request"))
    }


    private fun postOrder(body: String) = RestAssured
        .given()
        .body(body)
        .contentType(ContentType.JSON)
        .`when`()
        .port(springBootPort)
        .post("/orders")
}
