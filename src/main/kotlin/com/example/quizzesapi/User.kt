package com.example.quizzesapi

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.Valid
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(name = "users")
data class User(
    @Id
    val id: UUID = UUID.randomUUID(),
    val name: String,
    @Email
    val email: String,
    val active: Boolean
)

@Repository
interface UserRepository: JpaRepository<User, UUID>{
    fun existsByName(name: String): Boolean
    fun existsByNameAndIdNot(name: String,id: UUID):Boolean
}

data class UserReq(
    @field:[NotNull NotBlank]
    val email: String,
    @field:[NotNull]
    val active: Boolean,
    @field:[NotNull NotBlank Size(min = 10, max = 35)]
    val name: String
)

@RestController
@RequestMapping("users")
class UserController(
     val userRepository: UserRepository
     ) {
     @GetMapping
     fun index() = ResponseEntity.ok(userRepository.findAll())

     @PostMapping
     fun create(@Valid @RequestBody userReq: UserReq): ResponseEntity<User> {
         if (userRepository.existsByName(userReq.name))
             throw ResponseStatusException(HttpStatus.CONFLICT, "User alredy exists")
       val user = User(
           name = userReq.name,
           email = userReq.email,
           active = true
       )
         userRepository.save(user)
         return ResponseEntity(user,HttpStatus.CREATED)
        }
    @GetMapping
    fun show(@PathVariable id: UUID): ResponseEntity<User>{
        val user = getUser(id)
        return ResponseEntity.ok(user)
    }

    @PutMapping
    fun update(@PathVariable id: UUID, @Valid @RequestBody userReq: UserReq): ResponseEntity<User> {
        val user = getUser(id)
        if (userRepository.existsByNameAndIdNot(userReq.name,id))
            throw ResponseStatusException(HttpStatus.CONFLICT,"User alredy exists")

        val updatedUser = user.copy(
            name = userReq.name,
            email = userReq.email,
            active = userReq.active
        )
        userRepository.save(updatedUser)
        return ResponseEntity.ok(updatedUser)
    }

    @DeleteMapping
    fun delete(@PathVariable id: UUID): ResponseEntity<User>{
        val user = getUser(id)
        userRepository.delete(user)
        return ResponseEntity.noContent().build()
    }

    private fun getUser(id: UUID): User{
        return userRepository.findByIdOrNull(id)?:
        throw ResponseStatusException(HttpStatus.NOT_FOUND,"User not found")
    }
}