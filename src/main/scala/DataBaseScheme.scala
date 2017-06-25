import slick.jdbc.PostgresProfile.api._
import java.sql.Timestamp
import java.util.Date

case class Topic(id: Option[Long] = None, alias: String, email: String, content: String, topic: String, secret: Long, timestamp: Timestamp)
case class Reply(id: Option[Long] = None, topicID: Long, alias: String, email: String, content: String, secret: Long, timestamp: Timestamp)


trait DatabaseScheme {
  class TopicsTable(tag: Tag) extends Table[Topic] (tag, "topics") {
    def id = column[Long]("topic_id", O.PrimaryKey, O.AutoInc)
    def alias = column[String]("alias")
    def email = column[String]("email")
    def content = column[String]("content")
    def topic = column[String]("topic")
    def secret = column[Long]("secret")
    def timestamp = column[Timestamp]("timestamp")
    def * = (id.?, alias, email, content, topic, secret, timestamp) <> (Topic.tupled, Topic.unapply)
  }
  val topics = TableQuery[TopicsTable]

  class RepliesTable(tag: Tag) extends Table[Reply](tag, "replies") {
    def id = column[Long]("reply_id", O.PrimaryKey, O.AutoInc)
    def topicID = column[Long]("topic_id")
    def alias = column[String]("alias")
    def email = column[String]("email")
    def content = column[String]("content")
    def secret = column[Long]("secret")
    def timestamp = column[Timestamp]("timestamp")
    def * = (id.?, topicID, alias, email, content, secret, timestamp) <> (Reply.tupled, Reply.unapply)
  }
  val replies = TableQuery[RepliesTable]
}

object DateConversion {
  implicit def dateToTimestamp(date: Date): Timestamp = new Timestamp(date.getTime)
}
