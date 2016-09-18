package pl.akkomar.machinepark

import com.datastax.driver.core.Cluster

object CassandraTest extends App {


  val cluster = Cluster.builder()
    .addContactPoint("127.0.0.1")
    .build()
  val session = cluster.connect()

  val resultSet = session.execute("select release_version from system.local")
  val row = resultSet.one()
  println(row.getString("release_version"))
  println(row.getString("release_version"))
  println(row.getString("release_version"))


  cluster.close()




}
