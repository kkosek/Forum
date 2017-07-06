import scala.concurrent.Future
import slick.jdbc.H2Profile.api.Database
import scala.concurrent.ExecutionContext.Implicits.global

class ForumService(persister: ForumPersister) {
  val db: Database = Database.forConfig("mydb")

  def findTopic(id: Long): Future[Option[Topic]] = db.run(persister.findTopic(id))

  def findReply(id: Long): Future[Option[Reply]] = db.run(persister.findReply(id))

  def addTopic(topic: Topic): Future[Int] = db.run(persister.addTopic(topic))

  def addReply(reply: Reply): Future[Int] = db.run(persister.addReply(reply))

  def deleteTopic(topicToRemove: DeleteRequest): Future[Int] = db.run(persister.deleteTopic(topicToRemove))

  def updateTopic(topicToUpdate: UpdateRequest): Future[Option[Topic]] = db.run(persister.updateTopic(topicToUpdate))

  def getTopics(page: Long, limit: Long): Future[Seq[Topic]] =
    db.run(persister.getTopics(page, limit))

  def getRepliesForTopic(topicID: Long, replyID: Long): Future[Option[Seq[Reply]]] =
    db.run(persister.findTopic(topicID)).flatMap {
      case Some(s) => db.run(persister.getRepliesForTopic(topicID, replyID)).map(rs => Some(rs))
      case None => Future.successful(None)
    }

  def updateReply(reply: UpdateRequest): Future[Option[Reply]] = db.run(persister.updateReply(reply))

  def deleteReply(reply: DeleteRequest): Future[Int] = db.run(persister.deleteReply(reply))
}
