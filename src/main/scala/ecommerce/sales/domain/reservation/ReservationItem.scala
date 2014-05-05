package ecommerce.sales.domain.reservation

import ddd.support.domain.DomainEntity
import java.util.UUID
import ecommerce.sales.domain.productscatalog.ProductData

/**
 * Created by liaoshifu on 2014/5/5.
 */
case class ReservationItem(product: ProductData, quantity: Int) extends  DomainEntity {

  override def id: String = UUID.randomUUID().toString

  def increaseQuantity(addedQuantity: Int) = copy(quantity = quantity + addedQuantity)

  def productId = product.productId
}
