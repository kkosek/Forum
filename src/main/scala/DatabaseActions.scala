import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.http.scaladsl.model.StatusCodes._

import scala.concurrent.Future
import scala.util.Success

trait DatabaseActions extends DatabaseSetup {
  import DateConversion._
  implicit def toTopicWithReplies(s: Seq[(Topic, Reply)]): TopicWithReplies = {
    val unzippedList = s.toList.unzip
    TopicWithReplies(unzippedList._1.head, unzippedList._2)
  }

  implicit def ToTopicWithRepliesList(s: Seq[(Topic, Reply)]): List[TopicWithReplies] = {
    val list = s.toList.unzip
    val topics = list._1.distinct
    val replies = list._2
    topics.map(t => TopicWithReplies(t, replies.filter(r => r.topicID == t.id)))
  }

  def getTopic(id: Long) = {
    db.run(topics.filter(_.id === id).result.headOption) flatMap {
      case Some(s) => Future.successful(Some(s))
      case None    => Future.successful(None)
    }
  }

  def getAllTopics = {
    db.run(topics.result) flatMap { res =>
      Option(res) match {
        case Some(s) => Future.successful(Some(s))
        case None    => Future.successful(None)
      }
    }
  }

  def getRepliesForTopic(topicID: Long) = {
    db.run(replies.filter(_.topicID === topicID).result) flatMap { rs =>
      Option(rs) match {
       case Some(s) => Future.successful(Some(s))
       case None    => Future.successful(None)
      }
    }
  }

  def getReply(id: Long) = {
    db.run(replies.filter(_.id === id).result.headOption) flatMap {
      case Some(s) => Future.successful(Some(s))
      case None    => Future.successful(None)
    }
  }

  def getTopicWithReplies(id: Long) = {
    /*val get = for {
      t <- topics.filter(x => x.id === id)
      n <- replies.filter(x => x.topicId === t.id)
    } yield (t, n)

    val query = checkIfExists(id).flatMap {
      case Some(s) => get.result
      case None => DBIO.failed(new RuntimeException("Topic with this id already exists."))
    }
    db.run(query)*/

    db.run(topics.filter(_.id === id).result.headOption) flatMap {
      case Some(s) => Future.successful(Some(OK))
      case None    => Future.successful(None)
    }
  }

  def addTopic(topic: Topic) = {
    db.run((topics += topic))
  }

  def addReply(reply: Reply) = {
    db.run(replies += reply)
  }

  def deleteTopic(id: Long, secret: Long) = {
    db.run(topics.filter(_.id === id).result.headOption) flatMap {
      case Some(s) => {
        if (s.secret == secret) {
          db.run(topics.filter(_.id === id).delete) flatMap {
            case s if s == 0 => Future.successful(NotFound)
            case _ => Future.successful(OK)
          }
        }
        else Future.successful(NotFound)
      }
      case None    => Future.successful(NotFound)
    }
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
      n <- replies.sortBy(_.timestamp.desc).filter(x => x.topicID === t.id)
    } yield (t, n)

    val getRecentReplies = query
      .drop(rowsOnPage * (page - 1))
      .take(rowsOnPage).result
    db.run(getRecentReplies)
  }
}



