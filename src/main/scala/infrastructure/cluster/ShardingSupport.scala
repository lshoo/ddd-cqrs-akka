package infrastructure.cluster

import ddd.support.domain.AggregateRoot
import scala.reflect.ClassTag
import akka.actor.{Props, PoisonPill, ActorSystem}
import scala.concurrent.duration._
import akka.contrib.pattern.ShardRegion.Passivate
import akka.contrib.pattern.ClusterSharding

/**
 * Created by liaoshifu on 2014/5/6.
 */
trait ShardingSupport {

  def startSharding[T <: AggregateRoot[_]](implicit classTag: ClassTag[T], shardResolution: ShardResolution[T], system: ActorSystem) {
    startSharding(shardResolution)
  }

  def startSharding[T <: AggregateRoot[_]](shardResolution: ShardResolution[T], inactivityTimeout: Duration = 1.minutes)
                                          (implicit classTag: ClassTag[T], system: ActorSystem) {

    val arClass = classTag.runtimeClass.asInstanceOf[Class[T]]
    val arProps = Props(arClass, Passivate(stopMessage = PoisonPill), inactivityTimeout)

    ClusterSharding(system).start(
      typeName = arClass.getSimpleName,
      entryProps = Some(arProps),
      idExtractor = shardResolution.idExtractor,
      shardResolver = shardResolution.shardResolver
    )
  }
}
