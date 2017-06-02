import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

trait JSONFormats extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val topicFormat = jsonFormat6(Topic.apply)
  implicit val replyFormat = jsonFormat5(Reply.apply)
  implicit val topicWithReplyFormat = jsonFormat2(TopicWithReplies.apply)
}