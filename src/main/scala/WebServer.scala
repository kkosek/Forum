import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.concurrent.ExecutionContext.Implicits.global
import slick.jdbc.H2Profile.api.Database
import scala.io.StdIn
import scala.language.implicitConversions
import akka.http.scaladsl.unmarshalling.FromEntityUnmarshaller

object WebServer extends {
  val db = Database.forConfig("mydb")
  } with ServerRoute {
  def main(args: Array[String]) {
    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return

    db.close
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}


trait ServerRoute extends ForumDB with JSONFormats {
    val route =
      path("topic" / "\\d+".r) { id =>
        get {
          println("GET", id)
          complete {
            getTopicWithReplies(id.toInt)
          }
        }
      } ~
      path("add-topic") {
        post {
          entity(as[Topic]) { topic =>
            addTopic(topic)
            complete("This is post")
          }
        }
      }


}
