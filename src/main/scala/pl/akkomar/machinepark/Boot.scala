package pl.akkomar.machinepark

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import pl.akkomar.machinepark.api.{MachineParkApi, MachineUrl, RemoteHttpServer}
import pl.akkomar.machinepark.monitor.Monitor

import scala.concurrent.Future

object Boot extends App {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val machineParkApi = new MachineParkApi(new RemoteHttpServer())

  val monitor = new Monitor()

  val machinesUrls: Future[Seq[MachineUrl]] = machineParkApi.getMachinesList

  monitor.monitorMachines(machinesUrls, machineParkApi.getMachineStatus)

}





