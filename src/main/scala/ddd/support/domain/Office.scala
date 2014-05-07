package ddd.support.domain

import scala.reflect.ClassTag
import akka.actor._
import scala.concurrent.duration._
import akka.contrib.pattern.ClusterSharding
import infrastructure.actor.ActorContextCreationSupport
import akka.contrib.pattern.ShardRegion.Passivate

/**
 * Created by liaoshifu on 2014/5/6.
 */
object Office {

  def office[T <: AggregateRoot[_]]
    (implicit classTag: ClassTag[T], caseIdResolution: AggregateIdResolution[T], system: ActorRefFactory): ActorRef = {
    office[T]()
  }

  def office[T <: AggregateRoot[_]](inactivityTimeout: Duration = 1.minute)
                                   (implicit classTag: ClassTag[T], caseIdResolution: AggregateIdResolution[T], system: ActorRefFactory): ActorRef = {
    system.actorOf(Props(new Office[T](inactivityTimeout)), officeName(classTag))
  }

  def globalOffice[T](implicit classTag: ClassTag[T], system: ActorSystem): ActorRef = {
    ClusterSharding(system).shardRegion(officeName(classTag))
  }

  def officeName[T](classTag: ClassTag[T]) = classTag.runtimeClass.getSimpleName
}

class Office[T <: AggregateRoot[_]](inactivityTimeout: Duration = 1.minute)
                                   (implicit arClassTag: ClassTag[T], caseIdResolution: AggregateIdResolution[T])
  extends ActorContextCreationSupport with Actor with ActorLogging {

  def receive: Receive = {
    // TODO (passivation) in-between receiving Passivate and Terminated the office should buffer all incoming messages
    // for the clerk being passivated, when receiving Terminated it should flush the buffer
    case Passivate(stopMessage) =>
      dismiss(sender(), stopMessage)
    case msg =>
      val caseProps = Props(arClassTag.runtimeClass.asInstanceOf[Class[T]], Passivate(PoisonPill), inactivityTimeout)
      val clerk = assignClerk(caseProps, resolverCaseId(msg))
      clerk forward  msg
  }

  def resolverCaseId(msg: Any) = caseIdResolution.aggregateIdResolver(msg)

  def assignClerk(props: Props, caseId: String): ActorRef = getOrCreateChild(props, caseId)

  def dismiss(clerk: ActorRef, stopMessage: Any) {
    log.info(s"Passivating $sender()")
    clerk ! stopMessage
  }
}
