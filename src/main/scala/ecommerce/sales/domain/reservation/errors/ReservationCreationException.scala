package ecommerce.sales.domain.reservation.errors

import ddd.support.domain.error.DomainException

/**
 * Created by liaoshifu on 2014/5/5.
 */
case class ReservationCreationException(message: String)
  extends RuntimeException(message) with DomainException
