name := "political-feather"

version := "1.0-SNAPSHOT"

resolvers += "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
    "org.twitter4j" % "twitter4j-core" % "3.0.5",
    "org.twitter4j" % "twitter4j-stream" % "3.0.5",
    "org.reactivemongo" %% "play2-reactivemongo" % "0.10.0-SNAPSHOT"
)     

play.Project.playScalaSettings