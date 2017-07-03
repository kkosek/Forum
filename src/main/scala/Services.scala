import scala.concurrent.Future

trait Services extends Persisters {
  def getTopic(id: Long): Future[Option[Topic]] = db.run(findTopic(id))

  def getReply(id: Long): Future[Option[Reply]] = db.run(findReply(id))

  def addTopic(topic: Topic): Future[Int] = db.run(writeTopic(topic))

  def addReply(reply: Reply): Future[Int] = db.run(writeReply(reply))

  def removeTopic(topicToRemove: DataToRemove): Future[Int] = db.run(deleteTopic(topicToRemove))

  def updateTopic(topicToUpdate: DataToUpdate): Future[Int] = db.run(changeTopic(topicToUpdate))

  def getTopics(page: Long, limit: Long): Future[Seq[Topic]] = db.run(readTopics(page, limit))

  def getRepliesForTopic(topicID: Long, replyID: Long): Future[Seq[Reply]] =
    db.run(readRepliesForTopic(topicID, replyID))
}
