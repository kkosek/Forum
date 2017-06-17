import akka.http.scaladsl.server.Directives._
import scala.util.{Failure, Success}
import akka.http.scaladsl.model.StatusCodes._


trait Route extends DatabaseActions with Protocols {
  val route =
    delete {
      pathPrefix("delete" / "id") {
        pathPrefix(LongNumber) { id =>
          pathPrefix("secret") {
            path(LongNumber) { secret =>
              complete(deleteTopic(id, secret))
            }
          }
        }
      }
    } ~
    path("add-topic") {
      post {
        entity(as[Topic]) { topic =>
          complete(OK)
        }
      }
    } ~
    path("edit") {
      patch {
        entity(as[TopicEdition]) { topicEdition =>
          updateTopic(topicEdition)
          complete(OK -> "Topic was edited successfully.")
        }
      }
    } ~
    path("topics" / "(\\d+)".r) { page =>
      get {
        onComplete(getPaginatedResults(page.toLong)) {
          case Success(s) => complete(s: List[TopicWithReplies])
          case Failure(e) => complete {
            NotFound -> "Page does not exists."
          }
        }
      }
    } ~
    get {
      pathPrefix("topic") {
        pathEndOrSingleSlash {
          complete(getAllTopics)
        } ~
        pathPrefix(LongNumber) { topicId =>
          pathEndOrSingleSlash {
            complete(getTopic(topicId))
          } ~
          pathPrefix("reply") {
            pathEndOrSingleSlash {
              complete(getRepliesForTopic(topicId))
            } ~
            path(LongNumber) { replyId =>
              complete(getReply(replyId))
            }
          }
        }
      }
    }
}
