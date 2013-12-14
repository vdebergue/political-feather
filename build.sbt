name := "political-feather"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
    "org.twitter4j" % "twitter4j-core" % "3.0.5",
    "org.twitter4j" % "twitter4j-stream" % "3.0.5"
)     

play.Project.playScalaSettings
