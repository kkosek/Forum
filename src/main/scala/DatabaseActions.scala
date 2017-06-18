import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.http.scaladsl.model.StatusCodes._
import scala.concurrent.Future


trait DatabaseActions extends DatabaseSetup with Protocols {
  import DateConversion._

  implicit def toTopicWithRepliesList(sequence: Seq[(Topic, Reply)]) = {
    val s = sequence.groupBy(_._1) mapValues(_ map (_._2))
    val f = s.map { case (t, rs) => TopicWithReplies(t, rs)}
  }

  def getTopicWithReply(id: Long) = {
    db.run(topics.filter(_.id === id).result.headOption) flatMap {
      case Some(topic) => {
        db.run(replies.filter(_.topicID === id).result) flatMap { reps =>
          Future.successful(Right(TopicWithReplies(topic, reps)))
        }
      }
      case None => Future.successful(Left(ErrorMessage("There is no topic with this id.")))
    }
  }

  def getTopic(id: Long) = {
    db.run(topics.filter(_.id === id).result.headOption) flatMap {
      case Some(s) => Future.successful(Right(s))
      case None    => Future.successful(Left(ErrorMessage("There is no topic.")))
    }
  }

  def getAllTopics = {
    db.run(topics.result) flatMap {
        case s => Future.successful(s)
    }
  }

  def getRepliesForTopic(topicID: Long) = {
    db.run(topics.filter(_.id === topicID).result.headOption) flatMap {
       case Some(s) => db.run(replies.filter(_.topicID === topicID).result)
         .flatMap(x => Future.successful(Right(x)))
       case None    => Future.successful(Left(ErrorMessage("There is no topic with this id.")))
      }
  }

  def getReply(id: Long) = {
    db.run(replies.filter(_.id === id).result.headOption) flatMap {
      case Some(s) => Future.successful(Right(s))
      case None    => Future.successful(Left(ErrorMessage("There is no reply with such id.")))
    }
  }

  def addTopic(topic: Topic) = {
    db.run((topics += topic))
  }

  def addReply(reply: Reply) = {
    db.run(replies += reply)
  }

  def validate(id: Long, secret: Long) = {
    db.run(topics.filter(_.id === id).result.headOption) flatMap {
      case Some(s) => {
        if (s.secret == secret) Future.successful(Right(true))
        else Future.successful(Left(ErrorMessage("Secret do not match")))
      }
      case None => Future.successful(Left(ErrorMessage("There is no topic with this id.")))
    }
  }

  def deleteTopic(id: Long, secret: Long) = {
    validate(id, secret).flatMap {
      case Left(l) => Future.successful(Left(l))
      case Right(r) => db.run(topics.filter(_.id === id).delete)
        .flatMap { x => Future.successful(Right(OK)) }
      }
  }

  def updateTopic(id: Long, secret: Long, content: Content) = {
    val update = topics.filter(_.id === id)
      .map(t => (t.content, t.timestamp))
      .update((content.content, new java.util.Date))

    validate(id, secret).flatMap {
      case Left(l) => Future.successful(Left(l))
      case Right(r) => db.run(update).flatMap { x => Future.successful(Right(OK)) }
    }
  }

  def getPaginatedResults(page: Long) = {
    val rowsOnPage = 10
    val query = (for {
      t <- topics
      n <- replies.sortBy(_.timestamp.desc).filter(x => x.topicID === t.id)
    } yield (t, n))

    db.run(query.result).flatMap {
      case s => Future.successful(s)
    }



/*    val getRecentReplies = query
      .drop(rowsOnPage * (page - 1))
      .take(rowsOnPage).result
    db.run(getRecentReplies)*/
  }
}



