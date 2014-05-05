package ecommerce.sales.domain.productscatalog

import java.util.Date
import ecommerce.sales.domain.productscatalog.ProductType.ProductType
import ecommerce.sales.sharekernel.Money

/**
 * Created by liaoshifu on 2014/5/5.
 */
case class ProductData(productId: String,
                        name: String,
                        productType: ProductType,
                        price: Money,
                        snapshotDate: Date = new Date())
