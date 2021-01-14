import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.kafka.clients.producer._
import scala.jdk.CollectionConverters._
import org.apache.commons.io.FileUtils

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
    "org.apache.kafka.common.serialization.StringSerializer"
  )

  
  val clientRegion = Regions.US_EAST_1;
  val bucketName = "flanasa-vehicle-positions";
  
  val s3Client = AmazonS3ClientBuilder.standard()
                  .withRegion(clientRegion)
                  .build();
  
  val producer = new KafkaProducer[String, String](props)

  val formatter = new SimpleDateFormat("YYYY/MM/dd/HH/mm/ss");

  
  while (true) {
    poll
    Thread.sleep(500)
  }

  private def poll = Future {
    try {
      val dateString = formatter.format(new Date());
      val fileObjKeyName = dateString + "/VP.pb";
      val tempFile = File.createTempFile("temp", ".pb");
      FileUtils.copyURLToFile(new URL("https://cdn.mbta.com/realtime/VehiclePositions.pb"), tempFile);
      val metadata = new ObjectMetadata();
      metadata.setContentType("application/octet-stream");
      val request = new PutObjectRequest(bucketName, fileObjKeyName, tempFile);
      request.setMetadata(metadata);
      s3Client.putObject(request)
      val res = s3Client.getUrl(bucketName, fileObjKeyName);
      val record = new ProducerRecord[String, String](topic, dateString, res.toString());
      producer.send(record)
      tempFile.delete()
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }
}
