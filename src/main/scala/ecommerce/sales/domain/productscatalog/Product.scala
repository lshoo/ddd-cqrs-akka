package ecommerce.sales.domain.productscatalog

import akka.persistence.EventsourcedProcessor
import ddd.support.domain.{AggregateState, AggregateRoot}
import akka.actor.ActorLogging
import ecommerce.sales.domain.productscatalog.ProductType.ProductType
import ecommerce.sales.sharekernel.Money

/**
 * Created by liaoshifu on 2014/5/5.
 */
class Product extends AggregateRoot[ProductState] with EventsourcedProcessor with ActorLogging {

  override def receiveCommand: Product#Receive = ???

  override def receiveRecover: Product#Receive = ???

  override val factory: AggregateRootFactory = ???
}

case class ProductState(id: String,
                        name: String,
                        productType: ProductType,
                        price: Money)
  extends AggregateState {

  override def apply = {
    case _ => this
  }
}
