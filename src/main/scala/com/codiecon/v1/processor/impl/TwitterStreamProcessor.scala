package com.codiecon.v1.processor.impl



import com.codiecon.v1.EsClient
import com.codiecon.v1.workflow.TwitterWorkFlow
import org.apache.spark.streaming.dstream.DStream
import org.slf4j.LoggerFactory

import scala.util.parsing.json.JSON


object TwitterStreamProcessor {
  val logger = LoggerFactory.getLogger(TwitterStreamProcessor.getClass)

  def processStream(data: DStream[String]): Unit = {
    logger.debug("data: {}", data)
    data.foreachRDD { rdd =>
      if (!rdd.partitions.isEmpty) {
        logger.debug("data size: {}, rdd number of partition: {}", rdd.count(), rdd.getNumPartitions)
        rdd.foreach { partitionOfRecords =>
          val esClient = EsClient.client
          logger.debug("partition is not empty, size: {}", partitionOfRecords.size)
          try {
            logger.debug("processing record: {}", partitionOfRecords)
            val mapRecord = JSON.parseFull(partitionOfRecords).get.asInstanceOf[Map[String, Any]]
            TwitterWorkFlow.processTweets(mapRecord, esClient)
          }
          catch {
            case ex: Exception => {
              logger.error("Elastic search client Exception: {}", ex)
            }
          }
        }
      }
      else {
        logger.info("empty partition")
      }
    }
  }
}
