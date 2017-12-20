package com.codiecon.v1

import java.net.InetAddress

import com.typesafe.config.{Config, ConfigFactory}
import org.elasticsearch.client.Client
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.transport.client.PreBuiltTransportClient

class EsManager {
  val conf: Config = ConfigFactory.load()
  val elasticSearchClusterName: String = conf.getString("compute-layer.elasticsearch-cluster-name")
  val elasticSearchPort: Int = conf.getInt("compute-layer.elasticsearch-port")
  val elasticSearchNodes: String = conf.getString("compute-layer.elasticsearch-nodes")
  lazy private val settings = Settings.builder().put("cluster.name", elasticSearchClusterName).build()
  private val port = elasticSearchPort
  private val nodes = List(elasticSearchNodes)
  private val addresses = nodes.map { host => new InetSocketTransportAddress(InetAddress.getByName(host), port) }
  val client: Client = new PreBuiltTransportClient(settings).addTransportAddresses(addresses: _*)
}

object EsClient extends EsManager
