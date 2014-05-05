package ecommerce.sales.domain.reservation

import ddd.support.domain.event.DomainEvent
import ddd.support.domain.{AggregateState, AggregateRoot}
import java.util.Date
import Reservation._
import ReservationStatus._
import ecommerce.sales.domain.productscatalog.{ProductType, ProductData}
import ecommerce.sales.sharekernel.Money
import ecommerce.sales.domain.reservation.errors.{ReservationOperationException, ReservationCreationException}

/**
 * Created by liaoshifu on 2014/5/5.
 */

/**
 * Reservation is just a "wish list". System can not guarantee that user buy desired product.
 * Reservation generates Offer VO, that is calculated based on current prices and current availability.
 *
 */
object Reservation {

  // Commands
  sealed trait Command {
    def reservationId: String
  }
  case class CreateReservation(reservationId: String, clientId: String) extends Command
  case class ReserveProduct(reservationId: String, productId: String, quantity: Int) extends Command
  case class CloseReservation(reservationId: String) extends Command

  // Events
  case class ReservationCreated(reservationId: String, clientId: String) extends DomainEvent
  case class ProductReserved(reservationId: String, product: ProductData, quantity: Int) extends DomainEvent
  case class ReservationClosed(reservationId: String) extends DomainEvent

}

class Reservation extends AggregateRoot[State] {

  override val factory: AggregateRootFactory = {
    case ReservationCreated(_, clientId) => State(clientId, Opened, items = List.empty, createDate = new Date())
  }

  override def receiveCommand: Receive = {
    case cmd: Command => cmd match {

      case CreateReservation(reservationId, clientId) =>
        if (initialized) {
          throw new ReservationCreationException(s"Reservation $reservationId alreadly exists")
        } else {
          raise(ReservationCreated(reservationId, clientId))
        }

      case ReservationProduct(reservationId, productId, quantity) =>
        if (state.status eq Closed) throw new ReservationOperationException(s"Reservation $reservationId is closed", reservationId)
        else {
          // TODO fetch product detail
          // TODO fetch price for the client
          val product = ProductData(productId, "productName", ProductType.Standard, Money(10))
          raise(ProductReserved(reservationId, product, quantity)) { event =>
            // customized handle of ProductReserved
          }
        }

      case CloseReservation(reservationId) =>
        raise(ReservationClosed(reservationId))
    }
  }
}

case class State(clientId: String,
                  status: ReservationStatus,
                  items: List[ReservationItem],
                  createDate: Date)
  extends AggregateState {

  override def apply = {
    case event @ ProductReserved(_, product, quantity) =>
      val newItems = items.find(item => item.productId == product.productId) match {
        case Some(orderLine) =>
          val index = items.indexOf(orderLine)
          items.updated(index, orderLine.increaseQuantity(quantity))
        case None =>
          ReservationItem(product, quantity) :: items
      }
      copy(items = newItems)

    case ReservationClosed(_) => copy(status = Closed)
  }
}
