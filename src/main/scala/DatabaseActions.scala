import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.http.scaladsl.model.StatusCodes._
import scala.concurrent.Future

trait DatabaseActions extends DatabaseSetup with Protocols {
  import DateConversion._
  val rowsOnPage = 10

  def toTopicWithRepliesList(sequence: Seq[(Topic, Reply)]): Seq[TopicWithReplies] = {
    val s = sequence.groupBy(_._1) mapValues(_ map (_._2))
    s.map { case (t, rs) => TopicWithReplies(t, rs)}
      .toSeq
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
    db.run(topics.result)
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
    val query = (for {
      r <- replies.sortBy(_.timestamp.desc)
      t <- topics.filter(_.id === r.topicID)
    } yield t).drop((page - 1) * rowsOnPage).take(rowsOnPage)

    db.run(query.result) flatMap { s =>
      Future.successful(s.distinct)
    }
  }

  def dropValue(size: Long, before: Long): Long = {
    val proportions = before / size
    (before - (rowsOnPage * proportions).floor).toLong
  }

  def getPaginationByTopic(id: Long) = {
    db.run(topics.size.result) flatMap {
      case length if (length > 0) => {
        db.run(topics.filter(_.id === id).result.headOption) flatMap {
          case Some(t) => {
            db.run(topics.sortBy(_.timestamp).filter(_.timestamp < t.timestamp).size.result) flatMap {
              before => db.run(topics.sortBy(_.timestamp)
                .drop(dropValue(length, before)).take(rowsOnPage).result) flatMap {t =>
                Future.successful(Right(t))
              }
            }
          }
          case None => Future.successful(Left(ErrorMessage("There is no topic with this id.")))
        }
      }
      case _ => Future.successful(Right(Seq()))
    }
  }
}



