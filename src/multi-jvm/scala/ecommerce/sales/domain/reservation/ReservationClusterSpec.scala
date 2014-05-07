package ecommerce.sales.domain.reservation

import akka.remote.testkit.MultiNodeSpec
import test.support.STMultiNodeSpec
import akka.testkit.ImplicitSender
import java.io.File
import org.apache.commons.io.FileUtils
import akka.remote.testconductor.RoleName
import akka.cluster.Cluster
import akka.actor.{ActorIdentity, Identify, Props}
import akka.persistence.journal.leveldb.{SharedLeveldbJournal, SharedLeveldbStore}
import akka.persistence.Persistence
import infrastructure.cluster.ShardingSupport

class ReservationGlobalOfficeSpecMultiJvmNode1 extends ReservationGlobalOfficeSpec
class ReservationGlobalOfficeSpecMultiJvmNode2 extends ReservationGlobalOfficeSpec

abstract class ReservationClusterSpec extends MultiNodeSpec(ReservationClusterConfig)
with STMultiNodeSpec with ImplicitSender with ShardingSupport {

  import ReservationClusterConfig._

  def initialParticipants = roles.size

  val storageLocations = List(
    "akka.persistence.journal.leveldb.dir",
    "akka.persistence.journal.leveldb-shared.store.dir",
    "akka.persistence.snapshot-store.local.dir").map(s => new File(system.settings.config.getString(s)))

  override protected def atStartup() {
    on(node1) {
      storageLocations.foreach(dir => FileUtils.deleteDirectory(dir))
    }
  }

  override protected def afterTermination() {
    on(node1) {
      storageLocations.foreach(dir => FileUtils.deleteDirectory(dir))
    }
  }

  def join(startOn: RoleName, joinTo: RoleName) {
    on(startOn) {
      Cluster(system) join node(joinTo).address
    }
    enterBarrier(startOn.name + "-joined")
  }

  def setupSharedJournal() {
    Persistence(system)
    on(node1) {
      system.actorOf(Props[SharedLeveldbStore], "store")
    }
    enterBarrier("persistence-started")

    system.actorSelection(node(node1) / "user" / "store") ! Identify(None)
    val sharedStore = expectMsgType[ActorIdentity].ref.get
    SharedLeveldbJournal.setStore(sharedStore, system)

    enterBarrier("after-1")
  }

  def joinCluster() {
    join(startOn = node1, joinTo = node1)
    join(startOn = node2, joinTo = node1)
    enterBarrier("after-2")
  }

  def on(nodes: RoleName*)(thunk: ⇒ Unit): Unit = {
    runOn(nodes: _*)(thunk)
  }

}