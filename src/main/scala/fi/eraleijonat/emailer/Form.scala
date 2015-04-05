package fi.eraleijonat.emailer

case class Field(id: String, name: String, required: Boolean = false)

trait Form {

  val fields: Set[Field]

  def fieldNames: Set[String] = fields.map(_.name)
  def requiredFields: Set[Field] = fields.filter(_.required)
  def optionalFields: Set[Field] = fields.filterNot(_.required)

  def fieldById(s: String): Field = fields.find(_.name == s).get
}