import akka.http.scaladsl.server.Directives._

trait ServerRoute extends ForumDB with JSONFormats {
    val route =
      path("topic" / "\\d+".r) { id =>
        get {
          complete {
            getTopicWithReplies(id.toLong)
          }
        }
      } ~
      path("add-topic") {
        post {
          entity(as[Topic]) { topic =>
            addTopic(topic)
            complete("This is post")
          }
        }
      } ~
      path("reply" / "\\d+".r) { id =>
        post {
          entity(as[Reply]) { reply =>
            addReply(reply)
            complete("sth")
          }
        }
      } ~
      path("delete" / "\\d+".r) { id =>
        post {
          entity(as[Secret]) { secret =>
            deleteTopic(id.toLong, secret)
            complete("sth")
          }
        }
      } ~
      path("edit") {
        post {
          entity(as[TopicEdition]) { topicEdition =>
            updateTopic(topicEdition)
            complete("sth")
          }
        }
      }

}
