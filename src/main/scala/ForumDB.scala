import scala.concurrent.Await
import scala.concurrent.duration.Duration
import slick.jdbc.H2Profile.api.Database
import slick.jdbc.PostgresProfile.api._

trait ForumDB extends DataBaseScheme {
  def db: Database

  def getTopicWithReplies(id: Int): TopicWithReplies = {
    val topics = TableQuery[TopicsTable]
    val replies = TableQuery[RepliesTable]
    val query = for {
      t <- topics if t.id === id
      r <- replies if r.topicId === id
    } yield(t, r)
    val futureResult = db.run(query.result)
    val finalResult = Await.result(futureResult, Duration.Inf)
    val res = finalResult.toList.unzip
    TopicWithReplies(res._1.head, res._2)
  }
}




