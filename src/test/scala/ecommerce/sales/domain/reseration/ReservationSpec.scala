package ecommerce.sales.domain.reseration

import com.typesafe.config.ConfigFactory
import akka.actor.{PoisonPill, Props, ActorSystem}
import support.EventsourcedAggregateRootSpec

import ReservationSpec._

/**
 * Created by liaoshifu on 2014/5/4.
 */
object ReservationSpec {
  val testSystem = {
    val config = ConfigFactory.parseString(
      """akka.loggers = ["akka.testkit.TestEventListener"]
        |akka.persistence.journal.plugin = "in-memory-journal"
      """.stripMargin
    )
    ActorSystem("OrderSpec", config)
  }
}

class ReservationSpec extends EventsourcedAggregateRootSpec(testSystem) {

  override val aggregateRootId = "reservation1"

  def getReservationActor(name: String) = {
    getActor(Props[Reservation])(name)
  }

    "An Reservation actor" must {
      "handle Reservation process" in {
        val reservationId = aggregateRootId
        var reservation = getReservationActor(reservationId)

        expectEventPersisted[ReservationCreated] {
          reservation ! CreateReservation(reservationId, "client1")
        }

        expectEventPersisted[ProductReserved] {
          reservation ! ReserveProduct(reservationId, "product1", 1)
        }

        // kill and recreate reservation actor
        reservation ! PoisonPill
        Thread.sleep(1000)
        reservation = getReservationActor(reservationId)

        val product2 = ProductData("product2", "productName", ProductType.Standard, Money(10))
        val quantity = 1
        expectEventPersisted(ProductReserved(reservationId, product2, quantity)) {
          reservation ! ReserveProduct(reservationId, "product2", quantity)
        }

        expectEventPersisted[ReservationClosed] {
          reservation ! CloseReservation(reservationId)
        }
      }
    }
}
