import akka.http.scaladsl.server.Directives._
import scala.util.{Failure, Success}
import akka.http.scaladsl.model.StatusCodes._

trait Route extends DatabaseActions with Protocols {
    val route =
      path("topic=(\\d+)".r) { id =>
        get {
          onComplete(getTopicWithReplies(id.toLong)) {
            case Success(s) => complete(s: TopicWithReplies)
            case Failure(e) => complete(NotFound -> "There is no topic with such id.")
          }
        }
      } ~
      path("add-topic") {
        post {
          entity(as[Topic]) { topic =>
            onComplete(addTopic(topic)) {
              case Success(s) => complete(Created -> "Topic was created.")
              case Failure(e) => complete(Forbidden -> "Topic with this id already exists")
            }
          }
        }
      } ~
      path("reply") {
        post {
          entity(as[Reply]) { reply =>
            addReply(reply)
            complete(OK -> "Added reply")
          }
        }
      } ~
      path("delete") {
        post {
          entity(as[IDWithSecret]) { i =>
            onComplete(deleteTopic(i.id, i.secret)) {
              case Success(s) => complete(OK -> "Item was deleted")
              case Failure(e) => complete(NotFound -> "Error: neither secret or id is valid.")
            }
          }
        }
      } ~
      path("edit") {
        post {
          entity(as[TopicEdition]) { topicEdition =>
            updateTopic(topicEdition)
            complete(OK -> "Topic was edited successfully.")
          }
        }
      } ~
      path("browse=(\\d+)".r) { page =>
        get {
          onComplete(getPaginatedResults(page.toLong)) {
            case Success(s) => complete(s: List[TopicWithReplies])
            case Failure(e) => complete {NotFound -> "Page does not exists."}
          }
        }
      }
}
