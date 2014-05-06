package infrastructure.actor

import akka.actor.{ActorContext, ActorLogging, Props, ActorRef}

/**
 * Created by liaoshifu on 2014/5/6.
 */
trait CreationSupport {
  def getChild(name: String): Option[ActorRef]
  def createChild(props: Props, name: String): ActorRef
  def getOrCreateChild(props: Props, name: String): ActorRef = getChild(name).getOrElse(createChild(props, name))
}

trait ActorContextCreationSupport extends CreationSupport {
  this: ActorLogging =>

  def context: ActorContext

  def getChild(name: String): Option[ActorRef] = context.child(name)

  def createChild(props: Props, name: String): ActorRef = {
    val actor = context.actorOf(props, name)
    log.info(s"Actor created $actor")
    actor
  }
}
