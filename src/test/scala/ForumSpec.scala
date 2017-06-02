import org.scalatest.{Matchers, WordSpec}
import org.scalatest.Inside._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.model.StatusCodes
import slick.jdbc.H2Profile.api.Database

class ForumSpec extends {
  val db = Database.forConfig("mydb")
  } with WordSpec with Matchers with ScalatestRouteTest with ServerRoute {
  "The service should" {
    "return non-empty topic with one reply for id=1" in {
      Get("/topic/1") ~> route ~> check {
        inside(responseAs[TopicWithReplies]) {
          case TopicWithReplies(topic, replies) =>
            inside(topic) { case Topic(id, alias, email, _, _, _) =>
              id should be(1)
              //TODO
            }
            replies should have length 1
        }
      }
    }
    "return one topic with two replies for id=2" in {
      Get("/topic/2") ~> route ~> check {
        inside(responseAs[TopicWithReplies]) {
          case TopicWithReplies(topic, replies) =>
            inside(topic) { case Topic(id, alias, email, _, _, _) =>
              id should be(2)
              //TODO
            }
            replies should have length 2
        }
      }
    }
    1
  }

  it
}