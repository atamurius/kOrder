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
@Autowired constructor(val dishRepo: DishRepo) {

    @GetMapping fun get(@PageableDefault(100) page: Pageable): Page<Dish> = dishRepo.findAll(page)
}

@RestController
@RequestMapping("/api/orders")
class OrderController
@Autowired constructor(val dishRepo: DishRepo,
                       val reservationRepo: ReservationRepo,
                       val orderRepo: OrderRepo) {

    fun notFound(msg: String) = ResponseEntity.status(404).body(msg)

    @PostMapping fun create(@RequestParam code: String) = orderRepo.save(Order(code)).let {
        ResponseEntity.created(URI("/api/orders/${it.id}")).body(it)
    }

    @GetMapping("/{id}") fun get(@PathVariable id: Long, @RequestParam code: String) =
            orderRepo.findByIdAndCode(id, code) ?: notFound("No order #$id found")

    @PutMapping("/{id}/{username}/{dish}") fun add(
            @PathVariable id: Long,
            @RequestParam code: String,
            @PathVariable username: String,
            @PathVariable dish: Long
    ): Any =
            orderRepo.findByIdAndCode(id, code)?.let { order ->
                val dishObj = dishRepo.findOne(dish)
                val reservation = reservationRepo.findByOrderAndUsernameAndDish(order, username, dishObj) ?:
                        Reservation(username, dishObj, 0, order)
                reservation.amount += 1
                return reservationRepo.save(reservation)
            } ?: notFound("No order #$id found")

    @DeleteMapping("/{id}/{username}/{dish}") fun delete(
            @PathVariable id: Long,
            @RequestParam code: String,
            @PathVariable username: String,
            @PathVariable dish: Long
    ): Any =
            orderRepo.findByIdAndCode(id, code)?.let { order ->
                val dishObj = dishRepo.findOne(dish)
                val reservation = reservationRepo.findByOrderAndUsernameAndDish(order, username, dishObj) ?:
                        Reservation(username, dishObj, 1, order)
                reservation.amount -= 1
                return if (reservation.amount > 0) {
                    reservationRepo.save(reservation)
                } else {
                    reservationRepo.delete(reservation.id)
                    ResponseEntity.noContent().build()
                }
            } ?: notFound("No order #$id found")
}