package com.codiecon.v1

import java.net.InetAddress

import com.typesafe.config.{Config, ConfigFactory}
import org.elasticsearch.client.Client
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.transport.client.PreBuiltTransportClient
import org.slf4j.{Logger, LoggerFactory}

class EsManager {
  val logger : Logger = LoggerFactory.getLogger("EsManager")
  logger.info(f"initialising es manager")
  val conf: Config = ConfigFactory.load()
  val elasticSearchClusterName: String = conf.getString("codiecon.elasticsearch-cluster-name")
  val elasticSearchPort: Int = conf.getInt("codiecon.elasticsearch-port")
  val elasticSearchNodes: String = conf.getString("codiecon.elasticsearch-nodes")
  lazy private val settings = Settings
    .builder()
    .put("cluster.name", elasticSearchClusterName)
    .put("client.transport.sniff", false)
    .build()
  private val port = elasticSearchPort
  private val nodes = List(elasticSearchNodes)
  private val addresses = nodes.map { host => new InetSocketTransportAddress(InetAddress.getByName(host), port) }
  logger.debug(f"trying to start client with settings: $settings, address: $addresses")
  val client: Client = new PreBuiltTransportClient(settings).addTransportAddresses(addresses: _*)
}

object EsClient extends EsManager
