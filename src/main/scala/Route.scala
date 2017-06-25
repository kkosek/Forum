import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes._

trait Route extends DatabaseActions with Protocols {
  val route =
    pathPrefix("topic") {
      pathEndOrSingleSlash {
        get { complete (getAllTopics) } ~
        post {
          entity(as[Topic]) { topic =>
            complete(Created, addTopic(topic))
          }
        }
      } ~
      pathPrefix(LongNumber) { topicID =>
        pathEndOrSingleSlash {
          get {
            complete(getTopic(topicID))
          } ~
          delete {
            entity(as[DataToRemove]) { topicToRemove =>
              complete(deleteTopic(topicToRemove))
            }
          } ~
          patch {
            entity(as[DataToUpdate]) { topicToUpdate =>
              complete(updateTopic(topicToUpdate))
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
      path("page" / LongNumber) { page =>
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
