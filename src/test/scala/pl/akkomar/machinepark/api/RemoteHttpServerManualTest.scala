package pl.akkomar.machinepark.api

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.Future

object RemoteHttpServerManualTest extends App {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val httpServer = new RemoteHttpServer()

  val machinesList = httpServer.getMachinesList().map { entity =>
    println("Machine list: " + entity)
  }

  val machineStatus = httpServer.getMachineStatus(MachineUrl("$API_ROOT/machine/0e079d74-3fce-42c5-86e9-0a4ecc9a26c5")).map { entity =>
    println("Machine status: " + entity)
  }

  Future.sequence(Seq(machinesList, machineStatus)).onComplete(_ => system.terminate())
}
