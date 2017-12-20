package com.codiecon.v1.processor

import org.apache.spark.streaming.dstream.DStream

trait StreamProcessor {
  def processStream(data: DStream[String])
}
