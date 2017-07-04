import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import scala.concurrent.ExecutionContext.Implicits.global

trait Route extends Services with Protocols {
  val route =
    pathPrefix("topic") {
      parameters('page.as[Long].?, 'limit.as[Long].?) { (optionalPage, optionalLimit) =>
        val page = optionalPage.getOrElse(1)
        val limit = optionalLimit.getOrElse(50)
        complete {
          getTopics(page, limit).map[ToResponseMarshallable] {
            case Some(s) => s
            case None    => ErrorMessage(ErrorMessage.page0)
          }
        }
      } ~
      pathEndOrSingleSlash {
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
                removeTopic(topicToRemove).map[ToResponseMarshallable] {
                  case n if n == 1 => NoContent
                  case _ => ErrorMessage(ErrorMessage.wrongTopicFormat)
                }
              }
            }
          } ~
          patch {
            entity(as[DataToUpdate]) { topicToUpdate =>
              complete {
                updateTopic(topicToUpdate).map[ToResponseMarshallable] {
                  case n if n == 1 => OK
                  case _ => (BadRequest, ErrorMessage(ErrorMessage.wrongTopicFormat))
                }
              }
            }
          }
        } ~
        pathPrefix("reply") {
          parameter('middleReplyID.as[Long]){ middleReplyID =>
            get {
              complete {
                getRepliesForTopic(topicID, middleReplyID).map[ToResponseMarshallable] {
                  case Some(s) => s
                  case None    => (NotFound, ErrorMessage(ErrorMessage.topicNotFound))
                }
              }
            }
          } ~
          pathEndOrSingleSlash {
            post {
              entity(as[Reply]) { reply =>
                complete {
                  addReply(reply).map[ToResponseMarshallable] {
                    case s if s == 1 => (Created, reply)
                    case _ => (BadRequest, ErrorMessage(ErrorMessage.wrongReplyFormat))
                  }
                }
              }
            } ~
            delete {
              entity(as[DataToRemove]) { reply =>
                complete {
                  removeReply(reply).map[ToResponseMarshallable] {
                    case n if n == 1 => NoContent
                    case _ => ErrorMessage(ErrorMessage.wrongReplyFormat)
                  }
                }
              }
            } ~
            patch {
              entity(as[DataToUpdate]) { reply =>
                complete {
                  updateReply(reply).map[ToResponseMarshallable] {
                    //reply to jest Reply to Update!
                    case n if n == 1 => (OK, reply)
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
      }
    }
}
