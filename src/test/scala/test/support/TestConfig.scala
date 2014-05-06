package test.support

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

/**
 * Created by liaoshifu on 2014/5/6.
 */
object TestConfig {
  def testSystem = {
    val config = ConfigFactory.parseString(
      """akka.loggers = ["akka.testkit.TestEventListener"]
        |akka.persistence.journal.plugin = "in-memory-journal"
      """.stripMargin)
    ActorSystem("Tests", config)
  }
}
