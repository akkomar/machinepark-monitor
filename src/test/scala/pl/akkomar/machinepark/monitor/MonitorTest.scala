package pl.akkomar.machinepark.monitor

import akka.Done
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import org.scalatest.FlatSpec
import org.scalatest.concurrent.Eventually
import org.scalatest.time.{Millis, Second, Span}
import pl.akkomar.machinepark.api.{MachineStatus, MachineUrl}

import scala.concurrent.Future
import scala.concurrent.duration._

class MonitorTest extends FlatSpec with Eventually {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  implicit override val patienceConfig =
    PatienceConfig(timeout = scaled(Span(1, Second)), interval = scaled(Span(5, Millis)))

  "Monitor" should "work fine" in {
    val alerts = scala.collection.mutable.MutableList.empty[Alert]
    val alertSink: Sink[Alert, Future[Done]] = Sink.foreach[Alert](alerts += _)
    val machineDetailsTicks = Source(List.fill(4)(()))

    val monitor = new Monitor(alertSink, machineDetailsTicks)

    val machineUrls = Future.successful(Seq(MachineUrl("1"), MachineUrl("2")))
    val statusFetcher = (url: MachineUrl) => Future.successful(MachineStatus("", url.rawUrl, "", "", "", 10, 12))
    monitor.monitorMachines(machineUrls, statusFetcher, 1.microsecond)

    eventually {
      assert(alerts.size == 4)
    }
  }
}
