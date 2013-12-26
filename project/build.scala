import com.typesafe.sbt.SbtStartScript
import sbt._
import Keys._
import org.scalatra.sbt._

object EraleijonatEmailerBuild extends Build {
  val Organization = "fi.eraleijonat"
  val Name = "Eraleijonat emailer"
  val Version = "0.1.0-SNAPSHOT"
  val ScalaVersion = "2.10.2"
  val ScalatraVersion = "2.2.2"

  lazy val project = Project (
    "eraleijonat-emailer",
    file("."),
    settings = Defaults.defaultSettings ++ SbtStartScript.startScriptForClassesSettings ++ ScalatraPlugin.scalatraWithJRebel ++ Seq(
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers += Classpaths.typesafeReleases,
      libraryDependencies ++= Seq(
        "org.scalatra" %% "scalatra" % ScalatraVersion,
        "org.scalatra" %% "scalatra-specs2" % ScalatraVersion % "test",
        "ch.qos.logback" % "logback-classic" % "1.0.6" % "runtime",
        "org.eclipse.jetty" % "jetty-webapp" % "8.1.8.v20121106" % "compile;container",
        "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "compile;container;provided;test" artifacts (Artifact("javax.servlet", "jar", "jar")),
        "net.databinder.dispatch" %% "dispatch-core" % "0.11.0"
      )
    )
  )
}
