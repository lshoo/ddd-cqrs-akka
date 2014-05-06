package infrastructure.cluster

import akka.contrib.pattern.ShardRegion._
import ddd.support.domain.AggregateIdResolution
import ddd.support.domain.AggregateIdResolution.AggregateIdResolver

import ShardResolution._

/**
 * Created by liaoshifu on 2014/5/6.
 */
object ShardResolution {

  type ShardResolutionStrategy = AggregateIdResolver => ShardResolver

  val defaultShardResolutionStrategy: ShardResolutionStrategy = {
    aggregateIdResolver => {
      case msg: Msg => Integer.toHexString(aggregateIdResolver(msg).hashCode).charAt(0).toString
    }
  }
}

abstract class ShardResolution[T] extends AggregateIdResolution[T] {

  def shardResolutionStrategy = defaultShardResolutionStrategy

  val shardResolver: ShardResolver = shardResolutionStrategy(aggregateIdResolver)

  val idExtractor: IdExtractor = {
    case msg: Msg => (aggregateIdResolver(msg), msg)
  }
}