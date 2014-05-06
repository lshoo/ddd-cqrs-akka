package ecommerce.sales.domain.offer.decorators

import ecommerce.sales.sharekernel.Money
import ecommerce.sales.domain.offer.Discounts.DiscountPolicy

/**
 * Created by liaoshifu on 2014/5/6.
 */
object VipDiscount extends ((Money, Money) => Option[DiscountPolicy] => DiscountPolicy) {

  override def apply(minimalThreshold: Money, discountValue: Money) = (innerPolicy) => {
    (product, quantity, regularCost) => {
      val baseValue  = innerPolicy.map(_(product, quantity, regularCost)).getOrElse(regularCost)
      if (baseValue > minimalThreshold) baseValue - discountValue
      else baseValue
    }
  }
}
