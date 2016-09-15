package pl.akkomar.machinepark.monitor

import akka.actor.{ActorSystem, Cancellable}
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{ActorAttributes, ActorMaterializer, Supervision}
import akka.{Done, NotUsed}
import pl.akkomar.machinepark.api.{MachineStatus, MachineUrl}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class Monitor(alertSink: Sink[Alert, Future[Done]] = Monitor.DefaultAlertSink,
              machineDetailsTicks: Source[Unit, _] = Monitor.DefaultMachineDetailsTicks)
             (implicit val actorSystem: ActorSystem, val materializer: ActorMaterializer, val executionContext: ExecutionContext) {

  def monitorMachines(machineUrls: Future[Seq[MachineUrl]], fetchStatus: MachineUrl => Future[MachineStatus], checkRepeatPeriod: FiniteDuration = 5.seconds): Unit = {
    // Stream of machine urls - repeated and flattened every 5 seconds
    val machineUrlsToCheck: Source[MachineUrl, NotUsed] = Source.fromFuture(machineUrls).flatMapConcat { urls =>
      Source.tick(0.seconds, checkRepeatPeriod, urls.toIndexedSeq)
    }.mapConcat(identity)

    val machineStatuses = machineDetailsTicks
      .zip(machineUrlsToCheck)
      .mapAsync(1) { case (_, machineUrl) => fetchStatus(machineUrl) }
      .withAttributes(ActorAttributes.supervisionStrategy(Supervision.resumingDecider))
    val alerts = machineStatuses.map(checkStatus).filter(_.isDefined).map(_.get)

    alerts.runWith(alertSink)
    //        machineStatuses.runForeach(status=>println("Got status: "+status))
  }

  private def checkStatus(status: MachineStatus): Option[Alert] = {
    if (status.current > status.current_alert) {
      Some(Alert("Current alert: " + status))
    } else {
      None
    }
  }
}

object Monitor {

  val DefaultAlertSink: Sink[Alert, Future[Done]] = Sink.foreach[Alert] { alert =>
    println("!!! Alert: " + alert)
  }

  // Source of ticks for throttling machine detail requests to 80/second
  val DefaultMachineDetailsTicks: Source[Unit, Cancellable] = Source.tick(0.seconds, (1 / 80.0).second, ())
}

case class Alert(message: String)
