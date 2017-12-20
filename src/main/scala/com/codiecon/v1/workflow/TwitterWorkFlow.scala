package com.codiecon.v1.workflow

import com.typesafe.config.{Config, ConfigFactory}
import org.elasticsearch.client.Client
import org.slf4j.{Logger, LoggerFactory}

object TwitterWorkFlow {

  val logger: Logger = LoggerFactory.getLogger(TwitterWorkFlow.getClass)
  val conf: Config = ConfigFactory.load()

  val wolverineAppUrl: String = conf.getString("compute-layer.wolverine-datasource")

  def processTweets(mapRecord: Map[String, Any], esClient: Client): Unit ={

  }

}
