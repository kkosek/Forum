import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.model.StatusCodes
import scala.concurrent.Future

trait DatabaseActions extends DatabaseSetup with Protocols {
  import DateConversion._
  val rowsOnPage = 10

  def getTopic(id: Long): Future[Either[ErrorMessage, Topic]] = {
    db.run(topics.filter(_.id === id).result.headOption) flatMap {
      case Some(s) => Future.successful(Right(s))
      case None    => Future.successful(Left(ErrorMessage("There is no topic with this id.")))
    }
  }

  def getAllTopics: Future[Seq[Topic]] = db.run(topics.result)

  def getRepliesForTopic(topicID: Long): Future[Either[ErrorMessage, Seq[Reply]]] = {
    db.run(topics.filter(_.id === topicID).result.headOption) flatMap {
       case Some(s) => db.run(replies.filter(_.topicID === topicID).result)
         .flatMap(x => Future.successful(Right(x)))
       case None    => Future.successful(Left(ErrorMessage("There is no topic with this id.")))
      }
  }

  def getReply(id: Long): Future[Either[ErrorMessage, Reply]] = {
    db.run(replies.filter(_.id === id).result.headOption) flatMap {
      case Some(s) => Future.successful(Right(s))
      case None    => Future.successful(Left(ErrorMessage("There is no reply with such id.")))
    }
  }

  def addTopic(topic: Topic): Unit = db.run(topics += topic)

  def addReply(reply: Reply): Unit = db.run(replies += reply)

  def validate(id: Long, secret: Long): Future[Either[ErrorMessage, Boolean]] = {
    db.run(topics.filter(_.id === id).result.headOption) flatMap {
      case Some(s) => {
        if (s.secret == secret) Future.successful(Right(true))
        else Future.successful(Left(ErrorMessage("Secret do not match")))
      }
      case None => Future.successful(Left(ErrorMessage("There is no topic with this id.")))
    }
  }

  def deleteTopic(topicToRemove: TopicToRemove): Future[Either[ErrorMessage, StatusCode]] = {
    validate(topicToRemove.id, topicToRemove.secret).flatMap {
      case Left(l) => Future.successful(Left(l))
      case Right(r) => db.run(topics.filter(_.id === topicToRemove.id).delete)
        .flatMap { x => Future.successful(Right(StatusCodes.OK))}
      }
  }

  def updateTopic(updatedTopic: UpdatedTopic): Future[Either[ErrorMessage, StatusCode]] = {
    val update = topics.filter(_.id === updatedTopic.id)
      .map(t => (t.content, t.timestamp))
      .update((updatedTopic.content, new java.util.Date))

    validate(updatedTopic.id, updatedTopic.secret).flatMap {
      case Left(l) => Future.successful(Left(l))
      case Right(r) => db.run(update).flatMap { x => Future.successful(Right(StatusCodes.OK)) }
    }
  }

  def getPaginatedResults(page: Long): Future[Either[ErrorMessage, Seq[Topic]]] = {
    val query = (for {
        r <- replies.sortBy(_.timestamp.desc)
        t <- topics.filter(_.id === r.topicID)
      } yield t).drop((page - 1) * rowsOnPage).take(rowsOnPage).distinct

    if (page <= 0) Future.successful(Left(ErrorMessage("Pages start from '1'.")))
    else db.run(query.result) flatMap( s => Future.successful(Right(s)))
  }

  def dropValue(size: Long, before: Long): Long = {
    val proportions = before / size
    (before - (rowsOnPage * proportions).floor).toLong
  }

  def getPaginatedResultsByTopic(id: Long): Future[Either[ErrorMessage, Seq[Reply]]] = {
    val repliesForSortedTopics = for {
      t <- topics.sortBy(_.timestamp)
      r <- replies.filter(_.topicID === t.id)
    } yield r

    db.run(topics.size.result) flatMap {
      case length if length > 0 => {
        db.run(topics.filter(_.id === id).result.headOption) flatMap {
          case Some(t) => {
            db.run(topics.sortBy(_.timestamp).filter(_.timestamp < t.timestamp).size.result) flatMap {
              before => db.run(repliesForSortedTopics
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



