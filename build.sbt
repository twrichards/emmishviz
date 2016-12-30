lazy val scalaV = "2.11.7"

ivyScala := ivyScala.value map {
  _.copy(overrideScalaVersion = true)
}

lazy val server = (project in file("server")).settings(
  scalaVersion := scalaV,
  scalaJSProjects := Seq(client),
  pipelineStages in Assets := Seq(scalaJSPipeline),
  //  pipelineStages := Seq(digest, gzip),
  compile in Compile <<= (compile in Compile) dependsOn scalaJSPipeline
).enablePlugins(PlayScala).dependsOn(crossJvm)

lazy val client = (project in file("client")).settings(
  scalaVersion := scalaV,
  persistLauncher := true,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.1",
    "be.doeraene" %%% "scalajs-jquery" % "0.9.1"
  )
).enablePlugins(ScalaJSPlugin, ScalaJSWeb).dependsOn(crossJS)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared")).
  settings(scalaVersion := scalaV)

lazy val crossJvm = shared.jvm
lazy val crossJS = shared.js

// loads the server project at sbt startup
onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value