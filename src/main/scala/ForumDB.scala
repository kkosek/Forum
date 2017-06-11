import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global

trait ForumDB extends DatabaseSetup {
  import DateConversion._
  implicit def toTopicWithReplies(s: Seq[(Topic, Reply)]): TopicWithReplies = {
    val unzippedList = s.toList.unzip
    TopicWithReplies(unzippedList._1.head, unzippedList._2)
  }

  implicit def ToTopicWithRepliesList(s: Seq[(Topic, Reply)]): List[TopicWithReplies] = {
    val list = s.toList.unzip
    val topics = list._1.distinct
    val replies = list._2
    val k = topics.map(t => TopicWithReplies(t, replies.filter(r => r.topicId == t.id)))
    println(k)
    k
  }


  def getTopicWithReplies(id: Long) = {
    //this doesn't work. why?
/*    val query2 = topics.filter(_.id === id)
        .map(x => (x, replies.filter(_.id === id)))
        .result*/

    val query = for {
      t <- topics.filter(x => x.id === id)
      n <- replies.filter(x => x.topicId === t.id)
    } yield (t, n)

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



