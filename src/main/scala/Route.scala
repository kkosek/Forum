import akka.http.scaladsl.server.Directives._
import scala.util.{Failure, Success}
import akka.http.scaladsl.model.StatusCodes._
import spray.json._


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
          }
        } ~
        pathPrefix("secret") {
          path(LongNumber) { secret =>
            delete {
              complete(deleteTopic(topicID, secret))
            } ~
            patch {
              entity(as[Content]) { content =>
                complete(updateTopic(topicID, secret, content))
              }
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
      pathPrefix("page" / LongNumber) { page =>
        get {
          val a = getPaginatedResults(page)
          println(a)
          complete(a)
        }
      }
    }
}
