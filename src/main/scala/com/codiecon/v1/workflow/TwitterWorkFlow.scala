package com.codiecon.v1.workflow

import java.io.{BufferedReader, InputStreamReader}

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.http.entity.StringEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.HttpClientBuilder
import org.elasticsearch.client.Client
import org.slf4j.{Logger, LoggerFactory}

import scala.util.parsing.json.JSON

object TwitterWorkFlow {

  val logger: Logger = LoggerFactory.getLogger(TwitterWorkFlow.getClass)
  val conf: Config = ConfigFactory.load()
  val mapper = new ObjectMapper()
  val objMapper: ObjectMapper = mapper.registerModule(DefaultScalaModule)

  val SENTIMENT_SCORE = "sentiment_score"
  val ID_STR = "id_str"
  val TEXT = "text"
  val INDEX_NAME = "twitter"
  val DOC_TYPE = "tweet"
  val wolverineAppUrl: String = conf.getString("codiecon.wolverine-datasource")

  def processTweets(mapRecord: Map[String, Any], esClient: Client): Unit = {
    logger.info(f"processing tweet: $mapRecord")
    var body = mapRecord.map { case (key, value) => key -> value }
    val text: String = mapRecord.get(TEXT) match {
      case Some(value) => value.toString
    }
    val idStr: String = mapRecord.get(ID_STR) match {
      case Some(value) => value.toString
    }
    val sentimentScore: Double = getSentiment(text)
    body = body + (SENTIMENT_SCORE -> sentimentScore)
    indexDocumentInElastic(indexName = INDEX_NAME, docType = DOC_TYPE, idStr, body, esClient)
  }

  def getSentiment(text: String): Double = {
    var sentimentScore: Double = 0.0
    logger.info(f"getting sentiment for text: $text from the url: $wolverineAppUrl")
    val entity = new StringEntity(text)
    val client = HttpClientBuilder.create().build();
    val request = new HttpPost(wolverineAppUrl);
    request.setEntity(entity)
    val response = client.execute(request);
    logger.debug(f"response from the peoplehum app for the url: $wolverineAppUrl " +
      f"request: $request, response:$response")
    val reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
    val responseAsAMap = JSON.parseFull(reader.readLine()).get.asInstanceOf[Map[String, Any]]
    logger.debug(f"response from the peoplehum app  casted into MAP, responseMap: $responseAsAMap")
    if( responseAsAMap.get("responseObject") != null){
      val responseObject = responseAsAMap.get("responseObject").get.asInstanceOf[Map[String, Any]]
      sentimentScore = responseObject.get("score") match {
        case Some(value) => {
          logger.debug(f"got ${responseObject.get("score")} in the response object. " +
            f"setting value as $value")
          value.asInstanceOf[Double]
        }
        case _ => {
          logger.debug(f"got ${responseObject.get("score")} in the response object. " +
            f" setting value as 0.0")
          0.0
        }
      }
    }
    sentimentScore
  }

  def indexDocumentInElastic(indexName: String, docType: String, id: String, body: Map[String, Any]
                             , esClient: Client): Unit = {
    logger.info(f"inserting document in elastic, index: $indexName, docType: $docType, body: $body")
    val jsonBody = objMapper.writeValueAsString(body)
    try {
      val response = esClient.prepareIndex(indexName, docType, id).setSource(jsonBody).get()
      logger.debug(f"inserted a doc index: $indexName, docType: $docType, body(JSON): $jsonBody" +
        f", response: $response")
      esClient.admin().indices().prepareRefresh(indexName).get()
    } catch {
      case ex: Exception => {
        logger.error(f"error inserting doc in elastic, $indexName, docType: $docType, body(JSON): $jsonBody" +
          f", ex:", ex)
      }
    }
  }
}
