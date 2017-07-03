import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.model.StatusCodes
import scala.concurrent.Future

trait DatabaseActions extends DatabaseSetup with Protocols {
  import DateConversion._
  val rowsOnPage = 10

  def getAllTopics: Future[Seq[Topic]] = db.run(topics.result)

  def getTopic(id: Long): Future[Option[Topic]] =
    db.run(topics.filter(_.id === id).result.headOption)

  def getRepliesForTopic(topicID: Long): Future[Option[Seq[Reply]]] = {
    db.run(topics.filter(_.id === topicID).result.headOption) flatMap {
       case Some(_) => db.run(replies.filter(_.topicID === topicID).result).map(t => Some(t))
       case None    => Future.successful(None)
    }
  }

  def getReply(id: Long): Future[Option[Reply]] =
    db.run(replies.filter(_.id === id).result.headOption)

  def addTopic(topic: Topic): Future[Int] =
    db.run(topics += topic)

  def addReply(reply: Reply): Future[Int] =
    db.run(replies += reply)

  def validate(id: Long, secret: Long): Future[Either[ErrorMessage, Boolean]] = {
    db.run(topics.filter(_.id === id).result.headOption) flatMap {
      case Some(s) => {
        if (s.secret == secret) Future.successful(Right(true))
        else Future.successful(Left(ErrorMessage("Secret do not match")))
      }
      case None => Future.successful(Left(ErrorMessage("There is no topic with this id.")))
    }
  }

  def deleteTopic(topicToRemove: DataToRemove): Future[Int] = {
    validate(topicToRemove.id, topicToRemove.secret).flatMap {
      case Left(l) => Future.successful(-1)
      case Right(r) => db.run(topics.filter(_.id === topicToRemove.id).delete)
    }
  }

  def updateTopic(topicToUpdate: DataToUpdate): Future[Int] = {
    val update = topics.filter(_.id === topicToUpdate.id)
      .map(t => (t.content, t.timestamp))
      .update((topicToUpdate.content, new java.util.Date))

    validate(topicToUpdate.id, topicToUpdate.secret).flatMap {
      case Left(l) => Future.successful(-1)
      case Right(r) => db.run(update)
    }
  }

  def getPaginatedResults(page: Long): Future[Option[Seq[Topic]]] = {
    val query = (for {
        r <- replies.sortBy(_.timestamp.desc)
        t <- topics.filter(_.id === r.topicID)
      } yield t).drop((page - 1) * rowsOnPage).take(rowsOnPage).distinct

    if (page <= 0) Future.successful(None)
    else db.run(query.result).map(ts => Some(ts))
  }

  def dropValue(size: Long, before: Long): Long = {
    val proportions = before / size
    (before - (rowsOnPage * proportions).floor).toLong
  }

  def getPaginatedResultsByTopic(id: Long): Future[Option[Seq[Reply]]] = {
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
                .drop(dropValue(length, before)).take(rowsOnPage).result).map(rs => Some(rs))
            }
          }
          case None => Future.successful(None)
        }
      }
      case _ => Future.successful(None)
    }
  }
}



