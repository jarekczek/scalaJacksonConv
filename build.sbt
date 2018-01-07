mainClass := Some("jarek.Main1")
libraryDependencies += "com.fasterxml.jackson.dataformat" % "jackson-dataformat-xml" % "2.8.9"
libraryDependencies += "com.fasterxml.jackson.dataformat" % "jackson-dataformat-avro" % "2.8.9"
libraryDependencies += "com.twitter" % "finagle-http_2.12" % "17.12.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"
libraryDependencies += "org.mockito" % "mockito-core" % "2.8.9" % "test"
libraryDependencies += "org.apache.avro" % "avro" % "1.8.2"