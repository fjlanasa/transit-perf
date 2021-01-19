import org.apache.kafka.clients.producer._
import scala.jdk.CollectionConverters._
import org.apache.commons.io.IOUtils

import java.net.URL;
import java.util.Properties
import java.io.File
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import java.text.SimpleDateFormat
import java.util.Date

object Main extends App {

  val props = new Properties()
  val topic = "vehicle-positions-download"
  props.put("bootstrap.servers", "localhost:9092")

  props.put(
    "key.serializer",
    "org.apache.kafka.common.serialization.StringSerializer"
  )
  props.put(
    "value.serializer",
    "org.apache.kafka.common.serialization.ByteArraySerializer"
  )
  
  val producer = new KafkaProducer[String, Array[Byte]](props)

  val formatter = new SimpleDateFormat("YYYY/MM/dd/HH/mm/ss");

  
  while (true) {
    poll
    Thread.sleep(1000)
  }

  private def poll = Future {
    try {
      val dateString = formatter.format(new Date());
      val bytes = IOUtils.toByteArray(new URL("https://cdn.mbta.com/realtime/VehiclePositions.pb").openStream());
      val record = new ProducerRecord[String, Array[Byte]](topic, dateString, bytes);
      producer.send(record)
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }
}
