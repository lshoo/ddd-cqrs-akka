package support

import akka.actor.{ActorContext, Props, ActorRef}

/**
 * Created by liaoshifu on 2014/5/4.
 */
trait CreationSupport {
  def getChild(name: String): Option[ActorRef]
  def createChild(props: Props, name: String): ActorRef
  def getOrCreateChild(props: Props, name: String): ActorRef = getChild(name).getOrElse(createChild(props, name))
}

trait ActorContextCreationSupport extends CreationSupport {
  def context: ActorContext

  override def getChild(name: String): Option[ActorRef] = context.child(name)
  override def createChild(props: Props, name: String): ActorRef = context.actorOf(props, name)

}
