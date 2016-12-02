package ws.cpcs.examples.korder

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import ws.cpcs.examples.korder.model.*
import java.util.*

@SpringBootApplication open class Application {

    @Bean
    open fun init(
            categoryRepo: CategoryRepo,
            dishRepo: DishRepo,
            orderRepo: OrderRepo,
            reservationRepo: ReservationRepo) = CommandLineRunner {

        val first = categoryRepo.save(Category("Первые блюда"))
        val main = categoryRepo.save(Category("Основные блюда"))
        val dessert = categoryRepo.save(Category("Десерты"))
        val drinks = categoryRepo.save(Category("Напитки"))

        val dishes = listOf(
                Dish(
                        name = "Суп гороховый",
                        price = 12.60,
                        category = first
                ),
                Dish(
                        name = "Борщ",
                        price = 15.90,
                        category = first
                ),
                Dish(
                        name = "Солянка",
                        price = 18.00,
                        category = first
                ),
                Dish(
                        name = "Картофельное пюре с котлетой",
                        price = 15.20,
                        category = main
                ),
                Dish(
                        name = "Рис и жюльен",
                        price = 19.90,
                        category = main
                ),
                Dish(
                        name = "Гречневая каша с сосиской",
                        price = 12.50,
                        category = main
                ),
                Dish(
                        name = "Макароны по-флотски",
                        price = 13.00,
                        category = main
                ),
                Dish(
                        name = "Пирог с яблоками",
                        price = 8.50,
                        category = dessert
                ),
                Dish(
                        name = "Компот",
                        price = 7.00,
                        category = drinks
                ),
                Dish(
                        name = "Чай",
                        price = 4.00,
                        category = drinks
                )
        )
        dishRepo.save(dishes)

        fun <T> List<T>.random() = this[Random().nextInt(this.size)]

        val users = listOf("Анна", "Алексей", "Василий")

        (1..50).forEach {
            val order = orderRepo.save(Order("0000"))
            reservationRepo.save((1..5).toList().map {
                Reservation(
                        username = users.random(),
                        dish = dishes.random(),
                        amount = Random().nextInt(5) + 1,
                        order = order)
            })
        }
    }
}

fun main(vararg args: String) {
    SpringApplication.run(Application::class.java, *args)
}