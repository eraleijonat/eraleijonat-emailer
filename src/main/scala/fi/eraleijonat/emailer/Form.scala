package fi.eraleijonat.emailer

case class Field(id: String, name: String, required: Boolean = false)

trait Form {

  val fields: Set[Field]

  val fieldNames: Set[String] = fields.map(_.name)
  val requiredFields: Set[Field] = fields.filter(_.required)
  val optionalFields: Set[Field] = fields.filterNot(_.required)

  def fieldById(s: String): Field = fields.find(_.name == s).get
}