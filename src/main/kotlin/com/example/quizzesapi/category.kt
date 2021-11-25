package com.example.quizzesapi

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

data class Category (
    val id: UUID = UUID.randomUUID(),
    val name:String
    )

val categories  = mutableListOf<Category>()

@RestController
@RequestMapping("categories")
class CategoryController {
    @GetMapping
    fun index() = categories
}