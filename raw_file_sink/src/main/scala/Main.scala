import java.time.Duration
import java.util.Properties

import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala._
import org.apache.kafka.streams.scala.kstream._
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}

import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.core.sync.RequestBody

object Main extends App {
  val bucketName = "flanasa-vehicle-positions"
  val region = Region.US_EAST_1;
  val s3 = S3Client
    .builder()
    .region(region)
    .build();

  import Serdes._

  val props: Properties = {
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, "raw-sink-application")
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    p
  }

  val builder: StreamsBuilder = new StreamsBuilder
  val downloads: KStream[String, Array[Byte]] = builder.stream[String, Array[Byte]]("vehicle-positions-download")
  downloads.foreach((key, value) => {
    val objectRequest = PutObjectRequest
      .builder()
      .bucket(bucketName)
      .key(key + ".pb")
      .build();
  
    val res = s3.putObject(
      objectRequest,
      RequestBody.fromBytes(value)
    )
    print(res)
  });
  val streams: KafkaStreams = new KafkaStreams(builder.build(), props)
  streams.start()

  sys.ShutdownHookThread {
     streams.close(Duration.ofSeconds(10))
  }
}
