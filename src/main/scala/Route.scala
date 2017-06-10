import akka.http.scaladsl.server.Directives._
import scala.util.{Failure, Success}
import akka.http.scaladsl.model.StatusCodes._


trait ServerRoute extends ForumDB with JSONFormats {
    val route =
      path("topic" / "\\d+".r) { id =>
        get {
          onComplete(getTopicWithReplies(id.toLong)) {
            case Success(s) => complete(s: TopicWithReplies)
            case Failure(e) => complete { InternalServerError }
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
      } ~
      path("reply" / "\\d+".r) { id =>
        post {
          entity(as[Reply]) { reply =>
            addReply(reply)
            complete("sth")
          }
        }
      } ~
      path("delete" / "\\d+".r) { id =>
        post {
          entity(as[Secret]) { secret =>
            deleteTopic(id.toLong, secret)
            complete("sth")
          }
        }
      } ~
      path("edit") {
        post {
          entity(as[TopicEdition]) { topicEdition =>
            updateTopic(topicEdition)
            complete("sth")
          }
        }
      } ~
      path("browse") {
        get {
          onComplete(getPaginatedResults) {
            case Success(s) => complete(s)
            case Failure(e) => complete { InternalServerError }
          }
        }
      }


}
