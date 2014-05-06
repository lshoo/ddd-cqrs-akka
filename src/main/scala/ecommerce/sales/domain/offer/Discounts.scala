package ecommerce.sales.domain.offer

import ecommerce.sales.sharekernel.Money
import ecommerce.sales.domain.productscatalog.ProductData
import ecommerce.sales.domain.client.Client

/**
 * Created by liaoshifu on 2014/5/6.
 */
object Discounts {

  type Quantity = Int
  type InitialCost = Money
  type DiscountAmount = Money
  type DiscountPolicyFactory = Client => DiscountPolicy
  type DiscountPolicy = (ProductData, Quantity, InitialCost) => DiscountAmount
}
