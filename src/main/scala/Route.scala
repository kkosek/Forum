import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import scala.concurrent.ExecutionContext.Implicits.global

trait Route extends DatabaseActions with Protocols {
  val route =
    pathPrefix("topic") {
      pathEndOrSingleSlash {
        get { complete (getAllTopics) } ~
        post {
          entity(as[Topic]) { topic =>
            complete {
              addTopic(topic).map[ToResponseMarshallable] {
                case s if s == 1 => (Created, topic)
                case _ => (BadRequest, ErrorMessage(ErrorMessage.wrongTopicFormat))
              }
            }
          }
        }
      } ~
      pathPrefix(LongNumber) { topicID =>
        pathEndOrSingleSlash {
          get {
            complete {
              getTopic(topicID).map[ToResponseMarshallable] {
                case Some(s) => s
                case None    => (NotFound, ErrorMessage(ErrorMessage.topicNotFound))
              }
            }
          } ~
          delete {
            entity(as[DataToRemove]) { topicToRemove =>
              complete {
                deleteTopic(topicToRemove).map[ToResponseMarshallable] {
                  case n if n == 1 => NoContent
                  case _ => ErrorMessage(ErrorMessage.wrongReplyFormat)
                }
              }
            }
          } ~
          patch {
            entity(as[DataToUpdate]) { topicToUpdate =>
              complete {
                updateTopic(topicToUpdate).map[ToResponseMarshallable] {
                  case s if s == 1 => NoContent
                  case _ => (BadRequest, ErrorMessage(ErrorMessage.wrongTopicFormat))
                }
              }
            }
          }
        } ~
        pathPrefix("reply") {
          pathEndOrSingleSlash {
            get {
              complete {
                getRepliesForTopic(topicID).map[ToResponseMarshallable] {
                  case Some(s) => s
                  case None    => (NotFound, ErrorMessage(ErrorMessage.topicNotFound))
                }
              }
            } ~
            post {
              entity(as[Reply]) { reply =>
                complete {
                  addReply(reply).map[ToResponseMarshallable] {
                    case s if s == 1 => (Created, reply)
                    case _ => (BadRequest, ErrorMessage(ErrorMessage.wrongReplyFormat))
                  }
                }
              }
            }
          } ~
          path(LongNumber) { replyID =>
            get {
              complete {
                getReply(replyID).map[ToResponseMarshallable] {
                  case Some(s) => s
                  case None    => (NotFound, ErrorMessage(ErrorMessage.replyNotFound))
                }
              }
            }
          }
        }
      } ~
      path("page" / LongNumber) { page =>
        get {
          complete {
            getPaginatedResults(page).map[ToResponseMarshallable] {
              case Some(s) => s
              case None    => ErrorMessage(ErrorMessage.page0)
            }
          }
        }
      } ~
      path("id" / LongNumber) { id =>
        get {
          complete {
            getPaginatedResultsByTopic(id),map[ToResponseMarshallable] {
              case Some(s) => s
              case None => ErrorMessage(ErrorMessage.topicNotFound)
            }
          }
        }
      }
    }
}
