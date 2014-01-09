name := "political-feather"

version := "1.0-SNAPSHOT"

resolvers += "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
    "org.twitter4j" % "twitter4j-core" % "3.0.5",
    "org.twitter4j" % "twitter4j-stream" % "3.0.5",
    "org.reactivemongo" %% "play2-reactivemongo" % "0.10.2",
    "org.apache.lucene" % "lucene-snowball" % "3.0.3"
)     

play.Project.playScalaSettings
