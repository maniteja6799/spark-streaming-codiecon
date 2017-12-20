package com.codiecon.v1

import com.codiecon.v1.reciever.impl.KafkaStreamReceiver
import com.typesafe.config.ConfigFactory
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.LoggerFactory


object JobRunner {

  def main(args: Array[String]): Unit = {
    val logger = LoggerFactory.getLogger(JobRunner.getClass)

    val conf = ConfigFactory.load()

    val kafkaMetadataBrokerList = conf.getString("compute-layer.kafka-metadata-broker-list")
    val kafkaAutoOffsetReset = conf.getString("compute-layer.kafka-auto-offset-reset")
    val kafkaTopicName = conf.getString("compute-layer.kafka-topic-name")
    val sparkStreamingAppName = conf.getString("compute-layer.spark-streaming-appname")
    val sparkStreamingMaster = conf.getString("compute-layer.spark-streaming-master")

    val sparkConf = new SparkConf().setAppName(sparkStreamingAppName).setMaster(sparkStreamingMaster)
    val sc = new SparkContext(sparkConf)
    val ssc = new StreamingContext(sc, Seconds(5))

    val kafkaParams = Map(
      "metadata.broker.list" -> kafkaMetadataBrokerList,
      "auto.offset.reset" -> kafkaAutoOffsetReset
    )
    val topics = kafkaTopicName.split(",").toSet
    logger.debug("starting receiver")
    KafkaStreamReceiver.receiveStream(ssc, kafkaParams, topics)

    ssc.start() // Start the computation
    ssc.awaitTermination() // Wait for termination
  }
}

