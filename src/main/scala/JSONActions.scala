import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

trait MyJSONSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val topicFormat = jsonFormat6(Topic.apply)
  implicit val replyFormat = jsonFormat5(Reply.apply)
}