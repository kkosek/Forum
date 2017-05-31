import scala.concurrent.Await
import scala.concurrent.duration.Duration
import slick.jdbc.H2Profile.api.Database
import slick.jdbc.PostgresProfile.api._

trait ForumDB extends DataBaseScheme {
  def db: Database

  def getById(id: Int) = {
    val topics = TableQuery[TopicsTable]
    val replies = TableQuery[RepliesTable]
    val query = for {
      t <- topics if t.id === id
      r <- replies if r.topicId === t.id
    } yield (t)
    val futureResult = db.run(query.result)
    val finalResult = Await.result(futureResult, Duration.Inf)
    finalResult.head
  }
}




