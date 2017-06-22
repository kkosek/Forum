import akka.http.scaladsl.server.Directives._
import scala.util.{Failure, Success}
import akka.http.scaladsl.model.StatusCodes._
import spray.json._
import scala.concurrent.Future

trait Route extends DatabaseActions with Protocols {
  val route =
    pathPrefix("topic") {
      pathEndOrSingleSlash {
        get { complete(getAllTopics) } ~
        post {
          entity(as[Topic]) { topic =>
            complete(OK)
          }
        }
      } ~
      pathPrefix(LongNumber) { topicID =>
        pathEndOrSingleSlash {
          get {
            complete(getTopic(topicID))
          } ~
          delete {
            entity(as[TopicToRemove]) { topicToRemove =>
              complete(deleteTopic(topicToRemove))
            }
          } ~
          patch {
            entity(as[UpdatedTopic]) { updatedTopic: UpdatedTopic =>
              complete(updateTopic(updatedTopic))
            }
          }
        } ~
        pathPrefix("reply") {
          pathEndOrSingleSlash {
            get { complete(getRepliesForTopic(topicID)) }
          } ~
          path(LongNumber) { replyID =>
            get { complete(getReply(replyID)) }
          }
        }
      } ~
      pathPrefix("page") {
        path(LongNumber) { page =>
          get {
            complete(getPaginatedResults(page))
          }
        } ~
        path("id" / LongNumber) { id =>
          get {
            complete(getPaginatedResultsByTopic(id))
          }
        }
      }
    }
}
