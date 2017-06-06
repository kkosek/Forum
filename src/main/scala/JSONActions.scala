import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import java.sql.Timestamp
import scala.language.implicitConversions
import java.util.Date

trait JSONFormats extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val timestampFormat: JsonFormat[Timestamp] = jsonFormat[Timestamp](TimestampReader, TimestampWriter)
  implicit val topicFormat = jsonFormat7(Topic.apply)
  implicit val replyFormat = jsonFormat6(Reply.apply)
  implicit val secretFormat = jsonFormat1(Secret.apply)
  implicit val topicWithReplyFormat = jsonFormat2(TopicWithReplies.apply)
}

object TimestampReader extends RootJsonReader[Timestamp] {
  implicit def dateToTimestamp(date: Date) = {
    new Timestamp(date.getTime)
  }
  //FIXME
    def read(json: JsValue): Timestamp = {
      println("Reader working ...")
      json match {
        case time: JsValue => {
          new java.util.Date
        }
        case _ => {
          println("JSON error")
          throw DeserializationException("Wrong date format.")
        }
      }
    }
}

object TimestampWriter extends RootJsonWriter[Timestamp] {
  def write(timestamp: Timestamp): JsValue = {
    JsObject(
      "timestamp" -> JsString(timestamp.toString)
    )
  }
}

