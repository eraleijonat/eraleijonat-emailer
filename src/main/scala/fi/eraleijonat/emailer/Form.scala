package fi.eraleijonat.emailer

case class Field(id: String, name: String, required: Boolean = false)

trait Form {

  val fields: Set[Field]

  val fieldNames: Set[String] = fields.map(_.name)
  val requiredFields: Set[Field] = fields.filter(_.required)
  val optionalFields: Set[Field] = fields.filterNot(_.required)

  def fieldById(s: String): Field = fields.find(_.name == s).get
}

object JoinForm extends Form {

  val fields: Set[Field] = Set(
    Field("firstNames", "etunimet", true),
    Field("lastName", "sukunimi", true),
    Field("address", "osoite", true),
    Field("dob", "syntymäaika", true),

    Field("huoltaja-email", "huoltajan sähköpostiosoite"),
    Field("huoltaja-phone", "huoltajan puhelinnumero"),
    Field("huoltaja-name", "huoltajan nimi"),
    Field("email", "sähköpostiosoite"),

    Field("photo-publication-ok", "lupa valokuvien verkkojulkaisuun"),

    Field("details", "lisätiedot")
  )

}