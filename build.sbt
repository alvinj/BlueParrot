import AssemblyKeys._

// sbt-assembly
assemblySettings

// assembly uses this, so no spaces
name := "BlueParrot"

version := "1.1"

scalaVersion := "2.10.0"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.1.1"

//libraryDependencies += "com.typesafe.akka" % "akka-actor_2.10" % "2.2+"

//libraryDependencies += "com.typesafe.akka" % "akka-actor_2.10" % "RC1"

//libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.1+"

//libraryDependencies += "com.typesafe.akka" % "akka-actor_2.10" % "2.2-M1"

//libraryDependencies += "com.typesafe.akka" % "akka-actor_2.10" % "2.2+"

//libraryDependencies += "com.typesafe.akka" % "akka-actor_2.10" % "2.1.0"

