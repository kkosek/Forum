import java.sql.Timestamp

import org.scalatest.{Matchers, WordSpec}
import org.scalatest.Inside._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.model.{HttpEntity, StatusCodes}
import slick.jdbc.H2Profile.api.Database
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.unmarshalling
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import org.scalatest.Tag

class ForumSpec extends {
  val db = Database.forConfig("mydb")
  } with WordSpec with Matchers with ScalatestRouteTest with ServerRoute {

  "The service should" {
    "return non-empty topic with one reply for id=1" in {
      Get("/topic/1") ~> route ~> check {
        inside(responseAs[TopicWithReplies]) {
          case TopicWithReplies(topic, replies) =>
            inside(topic) { case Topic(id, alias, email, _, _, _, _) =>
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
            inside(topic) { case Topic(id, alias, email, _, _, _, _) =>
              id should be(2)
              //TODO
            }
            replies should have length 2
        }
      }
    }
    "remove topic and its replies from database" in {
      Post("/delete/4", HttpEntity(ContentTypes.`application/json`, """{"secret": 4}""")) ~> route ~> check {
        status shouldBe OK
      }
    }
    "add new topic when json posted on /add-topic" in {
      Post("/add-topic", HttpEntity(ContentTypes.`application/json`, """{"secret": 4, "email": "email@email.com", "alias": "andrzej", "id": 4, "content": "Problem", "topic": "Temat", "timestamp": "2017-06-02 13:13:13"}""")) ~> route ~> check {
        status shouldBe OK
        responseAs[String] shouldEqual "This is post"
      }
    }
    "add reply to topic when json posted on /reply-topic" in {
      Post("/reply/1", HttpEntity(ContentTypes.`application/json`, """{"id": 12, "topicId": 4, "alias": "bukiet", "email": "bukiet@onet.pl", "content": "Dont worry", "timestamp": "0"}""")) ~> route ~> check {
        status shouldBe OK
      }
    }
    "update row in database" in {
      Post("/edit", HttpEntity(ContentTypes.`application/json`, """{"id": 4, "secret": 4, "content": "Don't worry!"}""")) ~> route ~> check {
        status shouldBe OK
      }
    }
    1
  }

}