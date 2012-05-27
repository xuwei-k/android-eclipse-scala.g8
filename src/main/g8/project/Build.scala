import sbt._

import Keys._
import AndroidKeys._

object General {
  val settings = Defaults.defaultSettings ++ Seq (
    name := "$name$",
    version := "0.1",
    versionCode := 0,
    scalaVersion := "$scala_version$",
    platformName in Android := "android-$api_level$",
    shellPrompt in ThisBuild := { state =>
      Project.extract(state).currentRef.project +
      "git branch".lines_!.find{_.head == '*'}.map{_.drop(1)}.getOrElse("") + "> "
    },
    watchSources <++= (mainResPath in Android).map(_ ** "*.xml" get)
  )

  val proguardSettings = Seq (
    useProguard in Android := $useProguard$
  )

  lazy val genDir = SettingKey[File]("gen-dir")

  lazy val pathSettings = Seq(
    manifestTemplatePath in Android <<= (baseDirectory,manifestName in Android){_ / _},
    mainResPath in Android <<= (baseDirectory,resDirectoryName in Android).map{_ / _},
    mainAssetsPath in Android <<= (baseDirectory,assetsDirectoryName in Android){_ / _},
    genDir <<= baseDirectory(_ / "gen"),
    unmanagedSourceDirectories in Compile <+= genDir,
    managedScalaPath in Android <<= genDir,
    managedJavaPath in Android <<= genDir
  )

  lazy val fullAndroidSettings =
    General.settings ++
    AndroidProject.androidSettings ++
    TypedResources.settings ++
    proguardSettings ++
    AndroidManifestGenerator.settings ++
    AndroidMarketPublish.settings ++ Seq (
      keyalias in Android := "change-me",
      libraryDependencies += "org.specs2" %% "specs2" % "$specs2_version$" % "test"
    ) ++ pathSettings
}

object AndroidBuild extends Build {
  lazy val main = Project (
    "$name$",
    file("."),
    settings = General.fullAndroidSettings
  )

  lazy val tests = Project (
    "tests",
    file("tests"),
    settings = General.settings ++
               AndroidTest.androidSettings ++
               General.proguardSettings ++
               General.pathSettings ++ Seq (
      name := "$name$Tests",
      manifestPath in Android <<= (baseDirectory,manifestName in Android).map{(d,m) => Seq(d / m)}
    )
  ) dependsOn main
}
