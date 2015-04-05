package fi.eraleijonat.emailer

object JoinForm extends Form {

  override val fields: Set[Field] = Set(
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