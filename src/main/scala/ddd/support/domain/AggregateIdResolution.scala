package ddd.support.domain

import ddd.support.domain.AggregateIdResolution.AggregateIdResolver

/**
 * Created by liaoshifu on 2014/5/6.
 */
object AggregateIdResolution {
  type Command = Any
  type AggregateId = String
  type AggregateIdResolver = PartialFunction[Command, AggregateId]
}

trait AggregateIdResolution[T] {
  def aggregateIdResolver: AggregateIdResolver
}
