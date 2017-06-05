import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import java.sql.Timestamp

trait JSONFormats extends SprayJsonSupport with DefaultJsonProtocol {
  import MyJsonProtocol.TimestampJSONConversion._
  implicit val topicFormat = jsonFormat7(Topic.apply)
  implicit val replyFormat = jsonFormat5(Reply.apply)
  implicit val topicWithReplyFormat = jsonFormat2(TopicWithReplies.apply)
}

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit object TimestampJSONConversion extends RootJsonFormat[Timestamp] {
    def write(timestamp: Timestamp): JsValue = JsNumber("5")
    def read(json: JsValue): Timestamp = json match {
      case JsNumber(time) => new Timestamp(3452121)
      case _ => throw new DeserializationException("Wrong date format.")
    }
  }
}