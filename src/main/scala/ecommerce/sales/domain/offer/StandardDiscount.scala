package ecommerce.sales.domain.offer

import ecommerce.sales.sharekernel.Money

/**
 * Created by liaoshifu on 2014/5/6.
 */
object StandardDiscount extends ((Double, Int) => Discounts.DiscountPolicy) {

  def apply(discount: Double, minimalQuantity: Int) = {
    (product, quantity, regularCost) => {
      val discountRation = BigDecimal(discount / 100)
      if (quantity >= minimalQuantity) regularCost * discountRation
      else Money(0)
    }
  }
}
