import org.scalatest.{Matchers, WordSpec}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.model.StatusCodes
import slick.jdbc.H2Profile.api.Database

class ForumSpec extends {
  val db = Database.forConfig("mydb")
  } with WordSpec with Matchers with ScalatestRouteTest with ServerRoute {
  "The service should" {
    "return topic example" in {
      Get("/topic/0") ~> route ~> check {
        responseAs[Topic] shouldEqual Topic(0, "testAlias", "testEmail", "testContent", "testTopic", "0000")
      }
    }
    1
  }
}
