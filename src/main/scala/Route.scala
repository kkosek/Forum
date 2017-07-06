import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import scala.concurrent.ExecutionContext.Implicits.global

class Route extends ForumService(new ForumPersister) with Protocols {
  val route =
    pathPrefix("topic") {
      parameters('page.as[Long].?, 'limit.as[Long].?) { (optionalPage, optionalLimit) =>
        val page = optionalPage match {
          case Some(s) if s > 0 => s
          case _ => 1
        }

        val limit = optionalLimit match {
          case Some(s) if s > 0 => s
          case _ => 10
        }

        complete(getTopics(page, limit))
      } ~
      pathEndOrSingleSlash {
        post {
          entity(as[Topic]) { topic =>
            complete {
              addTopic(topic).map[ToResponseMarshallable] {
                case 1 => (Created, topic)
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
              findTopic(topicID).map[ToResponseMarshallable] {
                case Some(s) => s
                case None    => (NotFound, ErrorMessage(ErrorMessage.topicNotFound))
              }
            }
          } ~
          delete {
            entity(as[DeleteRequest]) { topic =>
              complete {
                deleteTopic(topic).map[ToResponseMarshallable] {
                  case 1 => NoContent
                  case _ => ErrorMessage(ErrorMessage.wrongTopicFormat)
                }
              }
            }
          } ~
          patch {
            entity(as[UpdateRequest]) { topic=>
              complete {
                updateTopic(topic).map[ToResponseMarshallable] {
                  case Some(t) => (OK, t)
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
                    case 1 => (Created, reply)
                    case _ => (BadRequest, ErrorMessage(ErrorMessage.wrongReplyFormat))
                  }
                }
              }
            } ~
            delete {
              entity(as[DeleteRequest]) { reply =>
                complete {
                  deleteReply(reply).map[ToResponseMarshallable] {
                    case 1 => NoContent
                    case _ => ErrorMessage(ErrorMessage.wrongReplyFormat)
                  }
                }
              }
            } ~
            patch {
              entity(as[UpdateRequest]) { reply =>
                complete {
                  updateReply(reply).map[ToResponseMarshallable] {
                    case Some(r) => (OK, r)
                    case None => (BadRequest, ErrorMessage(ErrorMessage.wrongReplyFormat))
                  }
                }
              }
            }
          } ~
          path(LongNumber) { replyID =>
            get {
              complete {
                findReply(replyID).map[ToResponseMarshallable] {
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
