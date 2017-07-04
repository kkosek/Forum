import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait Services extends Persisters {
  def getTopic(id: Long): Future[Option[Topic]] = db.run(findTopic(id))

  def getReply(id: Long): Future[Option[Reply]] = db.run(findReply(id))

  def addTopic(topic: Topic): Future[Int] = db.run(writeTopic(topic))

  def addReply(reply: Reply): Future[Int] = db.run(writeReply(reply))

  def removeTopic(topicToRemove: DataToRemove): Future[Int] = db.run(deleteTopic(topicToRemove))

  def updateTopic(topicToUpdate: DataToUpdate): Future[Option[Topic]] = db.run(changeTopic(topicToUpdate))

  def getTopics(page: Long, limit: Long): Future[Option[Seq[Topic]]] = {
    if(page == 0) Future.successful(None)
    else db.run(readTopics(page, limit)).map(ts => Some(ts))
  }

  def getRepliesForTopic(topicID: Long, replyID: Long): Future[Option[Seq[Reply]]] =
    db.run(findTopic(topicID)).flatMap {
      case Some(s) => db.run(readRepliesForTopic(topicID, replyID)).map(rs => Some(rs))
      case None => Future.successful(None)
    }

  def updateReply(reply: DataToUpdate): Future[Option[Reply]] = db.run(changeReply(reply))

  def removeReply(reply: DataToRemove): Future[Int] = db.run(deleteReply(reply))
}
