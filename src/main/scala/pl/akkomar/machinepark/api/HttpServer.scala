package pl.akkomar.machinepark.api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, HttpRequest}
import akka.stream.ActorMaterializer

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

trait HttpServer {
  def getMachinesList(): Future[HttpEntity.Strict]

  def getMachineStatus(machineUrl: MachineUrl): Future[HttpEntity.Strict]
}

class RemoteHttpServer(implicit val actorSystem: ActorSystem, val materializer: ActorMaterializer, val executionContext: ExecutionContext) extends HttpServer {
  val ApiRoot = "http://machinepark.actyx.io/api/v1"

  val Timeout = 300.millis

  override def getMachinesList() =
    Http()
      .singleRequest(HttpRequest(uri = s"$ApiRoot/machines"))
      .flatMap(_.entity.toStrict(Timeout))

  override def getMachineStatus(machineUrl: MachineUrl) =
    Http()
      .singleRequest(HttpRequest(uri = machineUrl.rawUrl.replace("$API_ROOT", ApiRoot)))
      .flatMap(_.entity.toStrict(Timeout))
}
