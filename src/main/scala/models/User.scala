package models

case class User(email: String, name: Option[String], password: String)