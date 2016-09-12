package pl.akkomar.machinepark.api

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.concurrent.{ExecutionContext, Future}

class MachineParkApi(val httpServer: HttpServer)
                    (implicit val actorSystem: ActorSystem, val materializer: ActorMaterializer, val executionContext: ExecutionContext) {

  def getMachinesList: Future[Seq[MachineUrl]] = {
    val rawMachinesList = httpServer.getMachinesList().map { responseEntity =>
      val responseContent = responseEntity.data.utf8String
      responseContent.parseJson.convertTo[Seq[String]]
    }
    rawMachinesList.map(_.map(rawUrl => MachineUrl(rawUrl)))
  }

  def getMachineStatus(machineUrl: MachineUrl): Future[MachineStatus] = {
    httpServer.getMachineStatus(machineUrl).map { responseEntity =>
      val responseContent = responseEntity.data.utf8String
      responseContent.parseJson.convertTo[MachineStatus](MachineStatus.machineStatusFormat)
    }
  }
}

case class MachineUrl(rawUrl: String) extends AnyVal

case class MachineStatus(timestamp: String, name: String, `type`: String, location: String, state: String, current_alert: BigDecimal, current: BigDecimal)

object MachineStatus {
  implicit val machineStatusFormat = jsonFormat7(MachineStatus.apply)
}
