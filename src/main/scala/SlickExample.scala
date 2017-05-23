import slick.jdbc.H2Profile.api.Database
import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Replies(tag: Tag) extends Table[(Int, Int, String, String, String)](tag, "REPLIES") {
  def id = column[Int]("reply_id", O.PrimaryKey)
  def topicId = column[Int]("topic_id")
  def alias = column[String]("alias")
  def email = column[String]("email")
  def content = column[String]("content")
  def * = (id, topicId, alias, email, content)
}

class Topics(tag: Tag) extends Table[(Int, String, String, String, String, String)] (tag, "TOPICS") {
  def id = column[Int]("topic_id", O.PrimaryKey)
  def alias = column[String]("alias")
  def email = column[String]("email")
  def content = column[String]("content")
  def topic = column[String]("topic")
  def secret = column[String]("secret")
  def * = (id, alias, email, content, topic, secret)
}

object SlickExample {
  def main(args: Array[String]) = {
    val db = Database.forConfig("mydb")
    val replies = TableQuery[Replies]
    val query = for (c <- replies) yield c.content
    val answer = query.result
    val futureValue: Future[Seq[String]] = db.run(answer)

    futureValue onComplete {
      case s => println(s)
    }
  }

}



