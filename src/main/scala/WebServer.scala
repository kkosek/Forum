import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.concurrent.ExecutionContext.Implicits.global
import slick.jdbc.H2Profile.api.Database
import scala.io.StdIn
import spray.json._

object WebServer extends {
  val db = Database.forConfig("mydb")
  } with ForumDB with MyJSONSupport {
  def main(args: Array[String]) {
    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()

    val route =
      get {
        path("topic" / "\\d+".r) { id =>
          println("GET", id)
          complete {
            getById(id.toInt).toJson
          }
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return

    db.close
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
