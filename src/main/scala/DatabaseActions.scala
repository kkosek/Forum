import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global

trait DatabaseActions extends DatabaseSetup {
  import DateConversion._
  implicit def toTopicWithReplies(s: Seq[(Topic, Reply)]): TopicWithReplies = {
    val unzippedList = s.toList.unzip
    TopicWithReplies(unzippedList._1.head, unzippedList._2)
  }

  def checkIfExists(id: Long) = topics.filter(_.id === id).exists.result

  implicit def ToTopicWithRepliesList(s: Seq[(Topic, Reply)]): List[TopicWithReplies] = {
    val list = s.toList.unzip
    val topics = list._1.distinct
    val replies = list._2
    topics.map(t => TopicWithReplies(t, replies.filter(r => r.topicId == t.id)))
  }

  def getTopicWithReplies(id: Long) = {
    val get = for {
      t <- topics.filter(x => x.id === id)
      n <- replies.filter(x => x.topicId === t.id)
    } yield (t, n)

    val query = checkIfExists(id).flatMap {
      case true => get.result
      case false =>  DBIO.failed(new RuntimeException("There is no topic with such id and secret."))
    }
    db.run(query)
  }

  def addTopic(topic: Topic) = {
    val insert = checkIfExists(topic.id).flatMap {
      case false => topics += topic
      case true => DBIO.failed(new RuntimeException("Topic with this id already exists."))
    }
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

  def getPaginatedResults(page: Long) = {
    val rowsOnPage = 10
    val query = for {
      t <- topics
      n <- replies.sortBy(_.timestamp.desc).filter(x => x.topicId === t.id)
    } yield (t, n)

    val getRecentReplies = query
      .drop(rowsOnPage * (page - 1))
      .take(rowsOnPage).result
    db.run(getRecentReplies)
  }
}



