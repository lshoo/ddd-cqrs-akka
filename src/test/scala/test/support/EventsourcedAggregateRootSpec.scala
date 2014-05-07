package test.support

import scala.language.postfixOps
import akka.actor.{ ActorSystem}
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfter, Matchers, BeforeAndAfterAll, WordSpecLike}
import akka.util.Timeout
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import scala.reflect.ClassTag
import scala.util.Failure
import ddd.support.domain.event.DomainEvent
import ddd.support.domain.AggregateIdResolution

/**
 * Created by liaoshifu on 2014/5/4.
 */
abstract class EventsourcedAggregateRootSpec[T](_system: ActorSystem)(implicit asClassTag: ClassTag[T]) extends TestKit(_system)
  with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll with BeforeAndAfter {

  val domain = asClassTag.runtimeClass.getSimpleName

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
    system.awaitTermination()
  }

  def expectEventPersisted[E <: DomainEvent](aggregateId: String)(when: Unit)(implicit t: ClassTag[E], idResolution: AggregateIdResolution[T]) {

    expectLogMessageFromAR("Event persisted: " + t.runtimeClass.getSimpleName, when)(aggregateId)
  }

  def expectEventPersisted[E <: DomainEvent](event: E)(aggregateId: String)(when: Unit)(implicit idResolution: AggregateIdResolution[T]) {
    expectLogMessageFromAR("Event persisted: " + event.toString, when)(aggregateId)

  }

  def expectLogMessageFromAR(messageStart: String, when: Unit)(aggregateId: String)(implicit idResolution: AggregateIdResolution[T]) {
    EventFilter.info(
      source = s"akka://Tests/user/$domain/$aggregateId",
      start = messageStart,
      occurrences = 1
    ).intercept {
      when
    }
  }

  def expectLogMessageFromOffice(messageStart: String)(when: Unit)(implicit idResolution: AggregateIdResolution[T]) {
    EventFilter.info(
      source = s"akka://Tests/user/$domain",
      start = messageStart,
      occurrences = 1
    ).intercept {
      when
    }
  }

  def expectedFailure[E](awaitable: Future[Any])(implicit t: ClassTag[E]) {
    implicit val timeout = Timeout(5, SECONDS)

    val future = Await.ready(awaitable, timeout.duration).asInstanceOf[Future[Any]]
    val futureValue = future.value.get
    futureValue match {
      case Failure(ex) if ex.getClass.equals(t.runtimeClass) => println("OK")
      case x => fail(s"Unexpected result: $x")
    }
  }

  def expectReply[O, R](obj: O)(when: => R): R = {
    val r = when
    expectMsg(20.seconds, obj)
    r
  }
}
