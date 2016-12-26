package ws.cpcs.examples.korder.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Configurable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import javax.persistence.*
import kotlin.jvm.Transient

@Configurable
@MappedSuperclass
abstract class Persistent<T : Persistent<T>> {

    @Id @GeneratedValue
    var id: Long? = null

    abstract protected val repo: JpaRepository<T, Long>

    override fun hashCode() = id?.hashCode() ?: 0
    override fun equals(other: Any?) = other?.javaClass == javaClass && other is Persistent<*> && other.id == id

    open fun delete() = repo.delete(id)

    open fun save(): T = repo.save(this as T)
}

@Entity
class Category(var title: String = "") : Persistent<Category>() {

    interface Repo : JpaRepository<Category, Long>

    @Autowired @Transient
    override lateinit var repo: Repo
}


@Entity
class Dish : Persistent<Dish>() {

    var name: String = ""
    var price: Double = 0.00
    @ManyToOne
    var category: Category? = null

    interface Repo : JpaRepository<Dish, Long>

    @Autowired @Transient
    override lateinit var repo: Repo
}

@Entity
class Reservation : Persistent<Reservation>() {

    var username: String = ""
    @ManyToOne
    lateinit var dish: Dish
    var amount: Int = 0
    @JsonIgnore @ManyToOne
    lateinit var order: Order

    interface Repo : JpaRepository<Reservation, Long> {
        fun findByOrderAndUsernameAndDish(order: Order, username: String, dish: Dish): Reservation?
    }
    @Autowired @Transient
    override lateinit var repo: Repo
}

fun Reservation.Repo.getOrCreate(o: Order, un: String, d: Dish): Reservation =
        findByOrderAndUsernameAndDish(o, un, d) ?: Reservation().apply {
            username = un
            order = o
            dish = d
        }

@Entity
@Table(name = "ORDERS")
@NamedEntityGraph(
        name = "Order.withReservations",
        attributeNodes = arrayOf(NamedAttributeNode("reservations"))
)
class Order(var code: String = "") : Persistent<Order>() {

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    var reservations: Set<Reservation> = emptySet()

    interface Repo : JpaRepository<Order, Long> {
        @EntityGraph(value = "Order.withReservations", type = EntityGraph.EntityGraphType.LOAD)
        fun findByIdAndCode(id: Long, code: String): Order?
    }
    @Autowired @Transient
    override lateinit var repo: Repo
}
