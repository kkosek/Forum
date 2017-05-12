
import java.sql.{DriverManager, Connection}

object DataBaseConnection {
  def main(args: Array[String]) = {
    val driver = "org.postgresql.Driver"
    val url = "jdbc:postgresql://localhost/forumdb"
    val username = "postgres"
    val password = "morela*brzoskwinia"
    // there's probably a better way to do this
    var connection:Connection = null

    try {
      // make the connection
      Class.forName(driver)
      connection = DriverManager.getConnection(url, username, password)

      // create the statement, and run the select query
      val statement = connection.createStatement()
      val resultSet = statement.executeQuery("SELECT topic FROM topics")
      while ( resultSet.next() ) {
        val host = resultSet.getString("topic")
        println("host, user = " + host)
      }
    } catch {
      case e => e.printStackTrace
    }
    connection.close()
  }
}
