import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

trait MyJSONSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val topicFormat: JsonFormat[Topic] = jsonFormat6(Topic.apply)
}