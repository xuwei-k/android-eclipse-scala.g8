import sbt._
object PluginDef extends Build {
  lazy val root = Project("plugins", file(".")) dependsOn(
    uri("git://github.com/jberkel/android-plugin#$android_plugin_version$")
  )
}

