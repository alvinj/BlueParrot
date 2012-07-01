import sbt._

object MyBuild extends Build {

  lazy val root = Project("root", file("."))
                    .dependsOn(soundPlayerProject)
                    .dependsOn(appleScriptUtils)

  lazy val soundPlayerProject = RootProject(uri("git://github.com/alvinj/SoundFilePlayer.git"))
  lazy val appleScriptUtils   = RootProject(uri("git://github.com/alvinj/AppleScriptUtils.git"))

}
