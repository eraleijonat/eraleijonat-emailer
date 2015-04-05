package fi.eraleijonat.emailer

case class Field(id: String, name: String, required: Boolean = false)

trait Form {

  val fields: Set[Field]

  def fieldIds: Set[String] = fields.map(_.id)
  def requiredFields: Set[Field] = fields.filter(_.required)
  def optionalFields: Set[Field] = fields.filterNot(_.required)

  def fieldById(s: String): Field = fields.find(_.id == s).get
}