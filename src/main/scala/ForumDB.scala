import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import slick.jdbc.H2Profile.api.Database
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global


trait ForumDB extends DataBaseScheme {
  import DataConversion._
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
  }

  def addReply(reply: Reply) = {
    val insert = replies += reply
    val result = db.run(insert)
  }

  def deleteTopic(id: Long, secret: Secret) = {
    val exists: DBIO[Boolean] = topics.filter(x => x.secret === secret.secret).filter(x => x.id === id).exists.result
    val query = exists.flatMap {
      case true => {
        topics.filter(_.id === id).delete andThen
        replies.filter(_.topicId === id).delete
      }
      case false =>
        DBIO.failed(new RuntimeException("There is no topic with such id and secret."))
    }
    db.run(query)
  }

  def updateTopic(topicEdition: TopicEdition) = {
    val exists: DBIO[Boolean] = topics.filter(x => x.secret === topicEdition.secret).filter(x => x.id === topicEdition.id).exists.result
    val query = exists.flatMap {
      case true => {
        topics.filter(_.id === topicEdition.id)
          .map(topic => (topic.content, topic.timestamp))
          .update((topicEdition.content, new java.util.Date))
      }
      case false =>
        DBIO.failed(new RuntimeException("There is no topic with such id and secret."))
    }
    db.run(query)
  }

}



