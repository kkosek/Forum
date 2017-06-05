import scala.concurrent.Await
import scala.concurrent.duration.Duration
import slick.jdbc.H2Profile.api.Database
import slick.jdbc.PostgresProfile.api._

trait ForumDB extends DataBaseScheme {
  def db: Database

  def getTopicWithReplies(id: Int): TopicWithReplies = {
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
    println("Added " + rowCount + " rows.")
    // println("Added topic: " + topic)
  }

  def addReply(reply: Reply) = ???
}



