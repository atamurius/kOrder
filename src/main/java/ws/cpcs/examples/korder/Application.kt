package ws.cpcs.examples.korder

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.EnableLoadTimeWeaving
import org.springframework.context.annotation.aspectj.EnableSpringConfigured
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import ws.cpcs.examples.korder.model.*
import java.util.*

@SpringBootApplication
@EnableJpaRepositories(considerNestedRepositories = true)
@EnableSpringConfigured
@EnableLoadTimeWeaving
open class Application {

    @Bean
    open fun init() = CommandLineRunner {

        val first = Category("Первые блюда").save()
        val main = Category("Основные блюда").save()
        val dessert = Category("Десерты").save()
        val drinks = Category("Напитки").save()

        val dishes = listOf(
                Dish().apply {
                    name = "Суп гороховый"
                    price = 12.60
                    category = first
                },
                Dish().apply {
                    name = "Борщ"
                    price = 15.90
                    category = first
                },
                Dish().apply {
                    name = "Солянка"
                    price = 18.00
                    category = first
                },
                Dish().apply {
                    name = "Картофельное пюре с котлетой"
                    price = 15.20
                    category = main
                },
                Dish().apply {
                    name = "Рис и жюльен"
                    price = 19.90
                    category = main
                },
                Dish().apply {
                    name = "Гречневая каша с сосиской"
                    price = 12.50
                    category = main
                },
                Dish().apply {
                    name = "Макароны по-флотски"
                    price = 13.00
                    category = main
                },
                Dish().apply {
                    name = "Пирог с яблоками"
                    price = 8.50
                    category = dessert
                },
                Dish().apply {
                    name = "Компот"
                    price = 7.00
                    category = drinks
                },
                Dish().apply {
                    name = "Чай"
                    price = 4.00
                    category = drinks
                }
        ).map { it.save() }

        val rand = Random();
        fun <T> List<T>.random() = this[rand.nextInt(this.size)]

        val users = listOf("Анна", "Алексей", "Василий")

        (1..50).forEach {
            val o = Order("0000").save()
            (1..5).forEach {
                Reservation().apply {
                    username = users.random()
                    dish = dishes.random()
                    amount = Random().nextInt(5) + 1
                    order = o
                }.save()
            }
        }
    }
}

fun main(vararg args: String) {
    SpringApplication.run(Application::class.java, *args)
}