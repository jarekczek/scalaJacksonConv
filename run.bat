@echo off
set cp=C:\Users\Jarek\.gradle\caches\modules-2\files-2.1\com.fasterxml.jackson.dataformat\jackson-dataformat-xml\2.8.9\ebf862c6050f7f1592980be1f5977859b28144f8\jackson-dataformat-xml-2.8.9.jar
set cp=%cp%;C:\Users\Jarek\.gradle\caches\modules-2\files-2.1\com.fasterxml.jackson.core\jackson-core\2.8.9\569b1752705da98f49aabe2911cc956ff7d8ed9d\jackson-core-2.8.9.jar
set cp=%cp%;C:\Users\Jarek\.gradle\caches\modules-2\files-2.1\com.fasterxml.jackson.core\jackson-databind\2.8.9\4dfca3975be3c1a98eacb829e70f02e9a71bc159\jackson-databind-2.8.9.jar
set cp=%cp%;D:\src\finagle-finagle-17.11.0\finagle-core\target\scala-2.12\finagle-core_2.12-17.11.0.jar
set cp=%cp%;D:\src\finagle-finagle-17.11.0\finagle-http\target\scala-2.12\finagle-http_2.12-17.11.0.jar
set cp=%cp%;D:\src\finagle-finagle-17.11.0\finagle-base-http\target\scala-2.12\finagle-base-http_2.12-17.11.0.jar
rem set cp=%cp%;D:\src\finagle-finagle-17.11.0\finagle-netty3\target\scala-2.12\finagle-netty3_2.12-17.11.0.jar
set cp=%cp%;D:\src\finagle-finagle-17.11.0\finagle-netty3-http\target\scala-2.12\finagle-netty3-http_2.12-17.11.0.jar
set cp=%cp%;D:\src\finagle-finagle-17.11.0\finagle-netty4-http\target\scala-2.12\finagle-netty4-http_2.12-17.11.0.jar
set cp=%cp%;D:\src\finagle-finagle-17.11.0\finagle-netty4\target\scala-2.12\finagle-netty4_2.12-17.11.0.jar
set cp=%cp%;D:\src\finagle-finagle-17.11.0\finagle-stats\target\scala-2.12\finagle-stats_2.12-17.11.0.jar
rem set cp=%cp%;C:\Users\Jarek\.ivy2\cache\com.twitter\util-codec_2.12\jars\util-codec_2.12-17.11.0.jar
set cp=%cp%;C:\Users\Jarek\.ivy2\cache\com.twitter\util-core_2.12\jars\util-core_2.12-17.11.0.jar
set cp=%cp%;C:\Users\Jarek\.ivy2\cache\com.twitter\util-stats_2.12\jars\util-stats_2.12-17.11.0.jar
set cp=%cp%;C:\Users\Jarek\.ivy2\cache\com.twitter\util-logging_2.12\jars\util-logging_2.12-17.11.0.jar
set cp=%cp%;C:\Users\Jarek\.ivy2\cache\com.twitter\util-app_2.12\jars\util-app_2.12-17.11.0.jar
set cp=%cp%;C:\Users\Jarek\.ivy2\cache\com.twitter\util-registry_2.12\jars\util-registry_2.12-17.11.0.jar
set cp=%cp%;C:\Users\Jarek\.ivy2\cache\com.twitter\util-lint_2.12\jars\util-lint_2.12-17.11.0.jar
set cp=%cp%;C:\Users\Jarek\.ivy2\cache\com.twitter\util-tunable_2.12\jars\util-tunable_2.12-17.11.0.jar
set cp=%cp%;C:\Users\Jarek\.ivy2\cache\io.netty\netty\bundles\netty-3.10.1.Final.jar
set cp=%cp%;C:\Users\Jarek\.ivy2\cache\io.netty\netty-transport\jars\netty-transport-4.1.16.Final.jar
set cp=%cp%;C:\Users\Jarek\.ivy2\cache\io.netty\netty-common\jars\netty-common-4.1.16.Final.jar
set classes=target/scala-2.12/classes
call D:\lang\scala\bin\scalac.bat -cp %cp% src/main/scala/jarek/*.* -d %classes%
if errorlevel 1 goto kon
call D:\lang\scala\bin\scala.bat -cp %cp%;%classes% jarek.MyServer
:kon
