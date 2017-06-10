import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import slick.jdbc.PostgresProfile.api._


import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success


trait ForumDB extends DatabaseSetup {
  import DateConversion._
  implicit def toTopicWithReplies(s: Seq[(Topic, Reply)]): TopicWithReplies = {
    val unzippedList = s.toList.unzip
    TopicWithReplies(unzippedList._1.head, unzippedList._2)
  }

  def getTopicWithReplies(id: Long) = {
    val query = for {
      t <- topics if t.id === id
      r <- replies if r.topicId === id
    } yield (t, r)
    db.run(query.result)
  }

  def addTopic(topic: Topic) = {
    val insert = topics += topic
    db.run(insert)
  }

  def addReply(reply: Reply) = {
    val insert = replies += reply
    db.run(insert)
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

  def getPaginatedResults: Future[Seq[Reply]] = {
    val getRecentReplies = replies.sortBy(_.timestamp).drop(5).take(5).result
    db.run(getRecentReplies)
  }


}



