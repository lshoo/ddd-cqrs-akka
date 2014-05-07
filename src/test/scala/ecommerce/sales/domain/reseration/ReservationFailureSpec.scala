package ecommerce.sales.domain.reseration

import test.support.EventsourcedAggregateRootSpec
import ecommerce.sales.domain.reservation.Reservation

import test.support.TestConfig._
import akka.util.Timeout
import akka.pattern.ask
import scala.concurrent.duration._
import ddd.support.domain.error.AggregateRootNotInitializedException
import ecommerce.sales.domain.reservation.Reservation.ReserveProduct
import ddd.support.domain.Office._

/**
 * Created by liaoshifu on 2014/5/7.
 */
class ReservationFailureSpec extends EventsourcedAggregateRootSpec[Reservation](testSystem) {

  "Reservation of product" must {
    "fail if Reservation does not exist" in {
      val reservationId = "reservation1"
      implicit val timeout = Timeout(5, SECONDS)

      // Use ask (?) to send a command and expect Failure(AggregateRootNotInitializedException) in the response
      expectedFailure[AggregateRootNotInitializedException] {
        office[Reservation] ? ReserveProduct(reservationId, "product1", 1)
      }
    }
  }
}
