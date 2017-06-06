import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import slick.jdbc.H2Profile.api.Database
import slick.jdbc.PostgresProfile.api._

import scala.util.Random

trait ForumDB extends DataBaseScheme {
  def db: Database

  def getTopicWithReplies(id: Long): TopicWithReplies = {
    val query = for {
      t <- topics if t.id === id
      r <- replies if r.topicId === id
    } yield (t, r)
    val futureResult = db.run(query.result)
    val finalResult = Await.result(futureResult, Duration.Inf)
    val res = finalResult.toList.unzip
    TopicWithReplies(res._1.head, res._2)
  }

  def addTopic(topic: Topic) = {
    val insert = topics += topic
    val result = db.run(insert)
    val rowCount = Await.result(result, Duration.Inf)
    println("Added " + rowCount + " rows to topics table.")
  }

  def addReply(reply: Reply) = {
    val insert = replies += reply
    val result = db.run(insert)
    val rowCount = Await.result(result, Duration.Inf)
    println("Added " + rowCount + " rows to replies table.")
  }

  def deleteTopic(id: Long) = {
    val removeTopics = topics.filter(_.id === id).delete
    val removeReplies = replies.filter(_.topicId === id).delete
    val result: Future[Int] = db.run(removeTopics andThen removeReplies)
    val affectedRows = Await.result(result, Duration.Inf)
    println("Deleted rows: " + affectedRows)
  }


}



