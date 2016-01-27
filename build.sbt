import sbt._

lazy val commonSettings = Seq(
  organization := "com.github.Jullil",
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.11.7",
  resolvers ++= Seq(
    "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
    "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
    "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"
  )
)

lazy val root = (project in file(".")).aggregate(restService).dependsOn(restService)
  .settings(commonSettings: _*)
  .settings(Seq(
    name := "balloooon-web-client",
    libraryDependencies ++= Seq(
      jdbc,
      cache,
      ws,
      specs2 % Test,
      "com.typesafe.akka" %% "akka-cluster" % "2.4.0",
      "com.typesafe.akka" %% "akka-contrib" % "2.4.0",
      "com.typesafe.akka" %% "akka-slf4j" % "2.4.0",
      "com.typesafe.akka" %% "akka-testkit" % "2.4.0" % Test,
      "org.postgresql" % "postgresql" % "9.4-1206-jdbc42",
      "org.webjars" % "bootstrap" % "3.0.0",
      "org.webjars" % "sockjs-client" % "1.0.2"
    ),
    PlayKeys.playMonitoredFiles ++= (sourceDirectories in(Compile, TwirlKeys.compileTemplates)).value
  ))
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)

lazy val restService = (project in file("rest-service"))
  .settings(commonSettings: _*)
  .settings(Seq(
    name := "balloooon-rest-service"
  ))

//lazy val root = project.in(file(".")).aggregates(core, util)

/*
lazy val webClient = (project in file("web-client"))
  .settings(commonSettings: _*)
  .settings(Seq(
    name := "balloooon-web-client",
    libraryDependencies ++= Seq(
      jdbc,
      cache,
      ws,
      specs2 % Test
      //  "postgresql" % "postgresql" % "9.4-1203-1.jdbc42"
    ),
    PlayKeys.playMonitoredFiles ++= (sourceDirectories in (Compile, TwirlKeys.compileTemplates)).value
  )).dependsOn(restService)
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)
*/


// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
//routesGenerator := InjectedRoutesGenerator

//PlayKeys.playMonitoredFiles ++= (sourceDirectories in (Compile, TwirlKeys.compileTemplates)).value
