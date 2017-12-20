package com.codiecon.v1.reciever.impl

import com.codiecon.v1.processor.impl.TwitterStreamProcessor
import com.codiecon.v1.reciever.StreamReceiver
import kafka.serializer.StringDecoder
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka.KafkaUtils
import org.slf4j.LoggerFactory

object KafkaStreamReceiver extends StreamReceiver {

  val logger = LoggerFactory.getLogger(KafkaStreamReceiver.getClass)

  def receiveStream(ssc: StreamingContext, param: Map[String, String], topics: Set[String]): Unit = {
    val stream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, param, topics)
    logger.debug("stream started")
    val data = stream.transform(x => x.values)
    stream.print()
    logger.debug(f"stream received - $data")
    TwitterStreamProcessor.processStream(data)

  }
}
