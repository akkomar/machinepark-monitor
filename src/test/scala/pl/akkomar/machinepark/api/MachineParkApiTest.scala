package pl.akkomar.machinepark.api

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.stream.ActorMaterializer
import org.scalatest.{AsyncFlatSpec, Matchers}

import scala.concurrent.Future

class MachineParkApiTest extends AsyncFlatSpec with Matchers {

  val httpServer = new HttpServer {
    override def getMachinesList() = Future.successful(
      HttpEntity.apply(
        ContentTypes.`application/json`,
        """[
          |"$API_ROOT/machine/1",
          |"$API_ROOT/machine/2",
          |"$API_ROOT/machine/3"
          |]""".stripMargin)
    )

    override def getMachineStatus(machineUrl: MachineUrl) = Future.successful(
      HttpEntity.apply(
        ContentTypes.`application/json`,
        """{
          |"name":"DMG DMU 40eVo [#50]",
          |"timestamp":"2016-09-11T09:54:06.781930",
          |"current":12.09,
          |"state":"working",
          |"location":"0.0,0.0",
          |"current_alert":14.0,
          |"type":"mill"
          |}""".stripMargin)
    )
  }
  implicit val system: ActorSystem = ActorSystem()
  val api = new MachineParkApi(httpServer)
//  implicit val executionContext = system.dispatcher

  implicit val materializer: ActorMaterializer = ActorMaterializer()


  "MachineParkApi" should "return list of machines" in {
    val machinesList = api.getMachinesList

    val expectedMachineUrls = (1 to 3).map(n => MachineUrl("$API_ROOT/machine/" + n))

    machinesList map { ml =>
      ml should contain theSameElementsAs expectedMachineUrls
    }
  }

  it should "return machine status" in {
    api.getMachineStatus(MachineUrl("")) map { status =>
      status shouldEqual MachineStatus("2016-09-11T09:54:06.781930", "DMG DMU 40eVo [#50]", "mill", "0.0,0.0", "working", 14, 12.09)
    }
  }
}
