import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global

class ForumPersister extends DatabaseScheme {
  import DateConversion._

  def findTopic(id: Long): DBIO[Option[Topic]] = topics.filter(_.id === id).result.headOption

  def readRepliesForTopic(topicID: Long): DBIO[Seq[Reply]] = replies.filter(_.topicID === topicID).result

  def findReply(id: Long): DBIO[Option[Reply]] = replies.filter(_.id === id).result.headOption

  def addTopic(topic: Topic): DBIO[Int] = topics += topic

  def addReply(reply: Reply): DBIO[Int] = replies += reply

  private def validateTopic(secret: Long, id: Long) =
    topics.filter(_.id === id).filter(_.secret === secret)

  private def validateReply(secret: Long, id: Long) =
    replies.filter(_.id === id).filter(_.secret === secret)

  def deleteTopic(topic: DeleteRequest): DBIO[Int] =
    validateTopic(topic.secret, topic.id).delete

  def updateTopic(topic: UpdateRequest): DBIO[Option[Topic]] =
    validateTopic(topic.secret, topic.id).map(t => (t.content, t.timestamp))
    .update((topic.content, new java.util.Date))
    .flatMap(x => findTopic(topic.id))

  def deleteReply(reply: DeleteRequest): DBIO[Int] =
    validateReply(reply.secret, reply.id).delete

  def updateReply(reply: UpdateRequest): DBIO[Option[Reply]] =
    validateReply(reply.secret, reply.id).map(r => (r.content, r.timestamp))
    .update((reply.content, new java.util.Date))
      .flatMap(x => findReply(reply.id))

  def getTopics(page: Long, limit: Long): DBIO[Seq[Topic]] =
    (for {
      r <- replies.sortBy(_.timestamp.desc)
      t <- topics.filter(_.id === r.topicID)
    } yield t).drop(page * limit).take(limit).result

  private def dropValue(size: Long, before: Long, limit: Long) = {
    val proportions = before / size
    (before - (limit * proportions).floor).toLong
  }

  private def getSortedRepliesForTopic(id: Long) =
    replies.filter(_.topicID === id).sortBy(_.timestamp)

  def getSizeOfRepliesForTopic(id: Long): DBIO[Int] =
    getSortedRepliesForTopic(id).size.result

  private def getPositionOfNthReply(topicID: Long, replyID: Long) =
    findReply(replyID).flatMap {
      case Some(r) => getSortedRepliesForTopic(topicID)
        .sortBy(_.timestamp).filter(_.timestamp < r.timestamp).size.result
      case None => replies.size.result
    }

  def getRepliesForTopic(topicID: Long, replyID: Long): DBIO[Seq[Reply]] =
    fin
    getSizeOfRepliesForTopic(topicID).flatMap {
      size => {
        getPositionOfNthReply (topicID, replyID).flatMap { position =>
          val f = dropValue(size, position, 10)
          getSortedRepliesForTopic(topicID).drop(f).take(10).result
        }
      }
    }

}
