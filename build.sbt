import AssemblyKeys._

// sbt-assembly
assemblySettings

// assembly uses this, so no spaces
name := "BlueParrot"

version := "1.0"

scalaVersion := "2.9.1"

// this publishes stuff for a repo, including md5, scaladoc, and a jar, but no dependencies
//publishTo := Some(Resolver.file("file",  new File("/Users/al/Projects/Scala/BlueParrot/jar")))

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
 
libraryDependencies += "com.typesafe.akka" % "akka-actor" % "2.0.1"

