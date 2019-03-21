organization := "org.goldenport"

name := "goldenport-atom"

version := "2.1.0"

scalaVersion := "2.12.7"

// crossScalaVersions := Seq("2.11.6", "2.10.5")

scalacOptions += "-deprecation"

scalacOptions += "-unchecked"

incOptions := incOptions.value.withNameHashing(true)

resolvers += "Asami Maven Repository" at "http://www.asamioffice.com/maven"

libraryDependencies += "org.goldenport" %% "goldenport-scala-lib" % "2.1.0"

//
publishTo := Some(Resolver.file("asamioffice", file("target/maven-repository")))
