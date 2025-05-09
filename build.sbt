organization := "org.goldenport"

name := "goldenport-atom"

version := "2.1.1"

scalaVersion := "2.12.13"

// crossScalaVersions := Seq("2.11.6", "2.10.5")

scalacOptions += "-deprecation"

scalacOptions += "-unchecked"

incOptions := incOptions.value.withNameHashing(true)

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

resolvers += "GitHab releases" at "https://raw.github.com/asami/maven-repository/2021-scala2.12/releases"

resolvers += "Asami Maven Repository" at "http://www.asamioffice.com/maven"

libraryDependencies += "org.goldenport" %% "goldenport-scala-lib" % "2.1.11"

//
val mavenrepo = settingKey[String]("mavenrepo")

mavenrepo := sys.env.getOrElse("PUBLISH_MAVEN_REPO", default = "target/maven-repository")

publishTo <<= mavenrepo { v: String =>
  Some(Resolver.file("file", file(v)))
}
