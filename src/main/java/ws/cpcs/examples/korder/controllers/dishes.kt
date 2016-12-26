package ws.cpcs.examples.korder.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ws.cpcs.examples.korder.model.*
import java.net.URI

@RestController
@RequestMapping("/api/dishes")
class DishController
@Autowired constructor(val dishes: Dish.Repo) {

    @GetMapping fun get(@PageableDefault(100) page: Pageable): Page<Dish> = dishes.findAll(page)
}

@RestController
@RequestMapping("/api/orders")
class OrderController
@Autowired constructor(val dishes: Dish.Repo,
                       val reservations: Reservation.Repo,
                       val orders: Order.Repo) {

    fun notFound(msg: String) = ResponseEntity.status(404).body(msg)

    class OrderSummary(
            val id: Long,
            val users: Set<String>,
            val amount: Double
    )

    @GetMapping
    fun list(@PageableDefault(10) page: Pageable): Page<OrderSummary> =
            orders.findAll(page).map {
                OrderSummary(
                        id = it.id!!,
                        users = it.reservations.groupBy { it.username }.keys,
                        amount = it.reservations.sumByDouble { it.amount * it.dish.price }
                )
            }

    @PostMapping fun create(@RequestParam code: String) = Order(code).save().let {
        ResponseEntity.created(URI("/api/orders/${it.id}")).body(it)
    }

    private fun <T> withOrder(id: Long, code: String, body: (Order) -> T) =
            orders.findByIdAndCode(id, code)?.let(body) ?: notFound("No order #$id found")

    @GetMapping("/{id}") fun get(@PathVariable id: Long, @RequestParam code: String) = withOrder(id, code) { it }

    @PutMapping("/{id}/{username}/{dish}") fun add(
            @PathVariable id: Long,
            @RequestParam code: String,
            @PathVariable username: String,
            @PathVariable dish: Long)
            =
            withOrder(id, code) { order ->
                reservations
                        .getOrCreate(order, username, dishes.getOne(dish))
                        .apply { amount += 1 }
                        .save()
            }

    @DeleteMapping("/{id}/{username}/{dish}") fun delete(
            @PathVariable id: Long,
            @RequestParam code: String,
            @PathVariable username: String,
            @PathVariable dish: Long
    ): Any? =
            withOrder(id, code) { order ->
                reservations.getOrCreate(order, username, dishes.getOne(dish))
                        .apply { amount -= 1 }
                        .let {
                            if (it.amount > 0) it.save()
                            else it.delete()
                        }
            }
}