import slick.jdbc.PostgresProfile.api._


trait Persisters extends DatabaseSetup {
  import DateConversion._

  def findTopic(id: Long): DBIO[Option[Topic]] = topics.filter(_.id === id).result.headOption

  def readRepliesForTopic(topicID: Long): DBIO[Seq[Reply]] = replies.filter(_.topicID === topicID).result

  def findReply(id: Long): DBIO[Option[Reply]] = replies.filter(_.id === id).result.headOption

  def writeTopic(topic: Topic): DBIO[Int] = topics += topic

  def writeReply(reply: Reply): DBIO[Int] = replies += reply

  def validateTopic(secret: Long, id: Long): Query[TopicsTable, Topic, Seq] =
    topics.filter(_.id === id).filter(_.secret === secret)

  def deleteTopic(topic: DataToRemove): DBIO[Int] =
    validateTopic(topic.secret, topic.id).delete

  def changeTopic(topic: DataToUpdate): DBIO[Int] =
    validateTopic(topic.secret, topic.id).map(t => (t.content, t.timestamp))
    .update((topic.content, new java.util.Date))

  def readTopics(page: Long, limit: Long): DBIO[Seq[Topic]] = {
    (for {
      r <- replies.sortBy(_.timestamp.desc)
      t <- topics.filter(_.id === r.topicID)
    } yield t).result
  }

  def dropValue(size: Long, before: Long, limit: Long): Long = {
    val proportions = before / size
    (before - (limit * proportions).floor).toLong
  }

  def readSortedRepliesForTopic(id: Long): Query[RepliesTable, Reply, Seq] =
    replies.filter(_.topicID === id).sortBy(_.timestamp)

  def getSizeOfRepliesForTopic(id: Long): DBIO[Int] =
    readSortedRepliesForTopic(id).size.result

  def readPositionOfNthReply(topicID: Long, replyID: Long): DBIO[Int] = {
    findReply(replyID).flatMap {
      case Some(r) => readSortedRepliesForTopic(topicID)
        .sortBy(_.timestamp).filter(_.timestamp < r.timestamp).size.result
    }
  }

  def readReplies(topicID: Long, replyID: Long): DBIO[Seq[Reply]] = {
    getSizeOfRepliesForTopic(topicID).flatMap {
      size => {
        readPositionOfNthReply (topicID, replyID).flatMap { position =>
          val f = dropValue(size, position, 10)
          readSortedRepliesForTopic(topicID).drop(f).take(10).result
        }
      }
    }
  }

}
