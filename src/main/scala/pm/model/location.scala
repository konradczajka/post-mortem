package pm.model

enum Direction:
  case UP, RIGHT, DOWN, LEFT

case class Coordinate(x: Int, y: Int):
  def next(d: Direction): Coordinate = d match
    case Direction.UP => Coordinate(x, y - 1)
    case Direction.DOWN => Coordinate(x, y + 1)
    case Direction.LEFT => Coordinate(x - 1, y)
    case Direction.RIGHT => Coordinate(x + 1, y)

enum Material(val passable: Boolean):
  case Floor extends Material(true)
  case Wall extends Material(false)

case class MapLevel(tiles: Vector[Vector[Material]]):
  def materialAt(c: Coordinate): Material =
  // TODO: walidacja, co robić poza granicami?
    tiles(c.y)(c.x)

  def width = tiles(0).size

  def height = tiles.size

object MapLevel:
  def empty(width: Int, height: Int): MapLevel = {
    val row = Vector.from(for
      x <- 0 until width
    yield if x == 0 || x == width - 1 then Material.Wall else Material.Floor)

    val tiles = Vector.from(for
      y <- 0 until height
    yield if y == 0 || y == height - 1 then Vector.fill(width)(Material.Wall) else row)

    MapLevel(tiles = tiles)
  }

case class Locations(private val map: MapLevel, private val actors: Map[Coordinate, ActorId]):
  def passable(c: Coordinate): Boolean =
    map.materialAt(c).passable

  def actorAt(c: Coordinate): Option[ActorId] =
    actors.get(c)

  def actorCoords(a: ActorId): Option[Coordinate] =
    actors.find(_._2 == a).map(_._1)

  def moveActor(actor: ActorId, to: Coordinate): Locations =
    actors.find(_._2 == actor) match
      case Some((c, a)) => Locations(map, actors - c + (to -> a))
      case None => this

  def withoutActor(actor: ActorId): Locations =
    actorCoords(actor) match
      case Some(c) => Locations(map, actors - c)
      case None => this

  // TODO: usunąć
  def print: String =
    def l = for
      y <- 0 until map.height
      x <- 0 until map.width
    yield (actorAt(Coordinate(x, y)) match
      case Some(PLAYER_ID) => "@"
      case Some(_) => "%"
      case None => map.materialAt(Coordinate(x, y)) match
        case Material.Floor => "."
        case Material.Wall => "#") + (if x == map.width - 1 then "\n" else "")

    l.foldLeft("")(_ + _)
