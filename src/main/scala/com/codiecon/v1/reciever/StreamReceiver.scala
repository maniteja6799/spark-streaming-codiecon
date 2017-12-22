package com.codiecon.v1.reciever

import org.apache.spark.streaming.StreamingContext

trait StreamReceiver {
  def receiveStream(ssc: StreamingContext, param: Map[String, String], topics: Set[String])
}
