//import sbt._
//import sbt.io.Path
//import sbt.librarymanagement.{Developer, Resolver, ScmInfo}
//import sbt.librarymanagement.ivy.Credentials

lazy val resolvers =  Seq(
  Opts.resolver.sonatypeReleases,
  Resolver.jcenterRepo
)

lazy val itSettings = Defaults.itSettings ++ Project.inConfig(IntegrationTest)(Seq(
  Keys.fork := true,
  Keys.parallelExecution := true
))

lazy val libraryDependencies = {
  val parquetVersion = "1.8.3"
  val sparkVersion = "2.3.1"
  val hadoopVersion = "2.9.1"
  Seq(
    "org.apache.parquet" % "parquet-hadoop" % parquetVersion,
    "org.apache.hadoop" % "hadoop-client" % hadoopVersion,
    "com.chuusai" %% "shapeless" % "2.3.3",
    "org.typelevel" %% "cats-core" % "1.1.0", // TODO probably will not be needed


    // tests
    "org.scalatest" %% "scalatest" % "3.0.5" % "test,it",
    "org.apache.spark" %% "spark-core" % sparkVersion % "it"
      exclude(org = "org.apache.hadoop", name = "hadoop-client"),
    "org.apache.spark" %% "spark-sql" % sparkVersion
      exclude(org = "org.apache.hadoop", name = "hadoop-client")
  )
}

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    Keys.name := "parquet4s",
    Keys.organization := "com.mjakubowski84",
    Keys.version := "0.1.0",
    Keys.scalaVersion := "2.11.12",
    Keys.crossScalaVersions := Seq("2.11.8", "2.12.6"),
    Keys.scalacOptions ++= Seq("-deprecation", "-target:jvm-1.8"),
    Keys.javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-unchecked", "-deprecation", "-feature"),
    Keys.resolvers := resolvers,
    Keys.concurrentRestrictions in Global += Tags.limit(Tags.Test, 1),
    Keys.libraryDependencies := libraryDependencies,
    Keys.credentials ++= Seq(
      Credentials(Path.userHome / ".ivy2" / ".publicCredentials"), // TODO
      Credentials(Path.userHome / ".sbt" / "pgp.credentials") // TODO
    ),
    PgpKeys.useGpg := true,
    Keys.pomIncludeRepository := { _ => false },
    Keys.licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT")),
    Keys.homepage := Some(url("https://github.com/mjakubowski84/parquet4s")),
    Keys.scmInfo := Some(
      ScmInfo(
        browseUrl = url("https://github.com/mjakubowski84/parquet4s"),
        connection = "scm:git@github.com:mjakubowski84/parquet4s.git"
      )
    ),
    Keys.developers := List(
      Developer(
        id    = "mjakubowski84",
        name  = "Marcin Jakubowski",
        email = "mjakubowski84@gmail.com",
        url   = url("https://github.com/mjakubowski84")
      )
    ),
    Keys.publishMavenStyle := true,
    Keys.publishTo := Some(
      if (isSnapshot.value)
        Opts.resolver.sonatypeSnapshots
      else
        Opts.resolver.sonatypeStaging
    ),
    Keys.publishArtifact in Test := false,
    Keys.publishArtifact in IntegrationTest := false
  )
  .settings(itSettings: _*)