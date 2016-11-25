package ws.cpcs.examples.korder.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.*

@MappedSuperclass abstract class WithId {
    @Id @GeneratedValue var id: Long = 0

    override fun hashCode() = id.hashCode()
    override fun equals(other: Any?) = other?.javaClass == javaClass && other is WithId && other.id == id
}

@Entity
class Category(var title: String = "") : WithId()

interface CategoryRepo : JpaRepository<Category, Long>


@Entity
class Dish(
        var name: String = "",
        var price: Double = 0.00,
        @ManyToOne var category: Category? = null) : WithId()

interface DishRepo : JpaRepository<Dish, Long>


@Entity
class Reservation(
        var username: String = "",
        @ManyToOne var dish: Dish? = null,
        var amount: Int = 0,
        @JsonIgnore @ManyToOne var order: Order? = null) : WithId()

interface ReservationRepo : JpaRepository<Reservation, Long> {
    fun findByOrderAndUsernameAndDish(order: Order, username: String, dish: Dish): Reservation?
}


@Entity
@Table(name = "ORDERS")
@NamedEntityGraph(
        name = "Order.withReservations",
        attributeNodes = arrayOf(NamedAttributeNode("reservations"))
)
class Order(
        var code: String = "",
        @OneToMany(mappedBy = "order") var reservations: Set<Reservation> = emptySet()) : WithId()

interface OrderRepo : JpaRepository<Order, Long> {
    @EntityGraph(value = "Order.withReservations", type = EntityGraph.EntityGraphType.LOAD)
    fun findByIdAndCode(id: Long, code: String): Order?
}
