package com.jagrosh.jmusicbot.db


import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.util.*

val db by lazy {
    val properties = Properties().apply { load(File("properties/config.properties").bufferedReader()) }
    Database.connect("jdbc:mysql://localhost:3306/siren", driver = "com.mysql.cj.jdbc.Driver", user = "root", password = properties["db.password"].toString())
}

fun createDb() {
    transaction(db) {
        // print sql to std-out
        addLogger(StdOutSqlLogger)

        SchemaUtils.create(WeatherLocationTable)


//        'select *' SQL: SELECT Cities.id, Cities.name FROM Cities
//        println("Cities: ${WeatherLocations.selectAll()}")
    }
}

object WeatherLocationTable : IntIdTable() {
    val discordUserId = varchar("discordUserId", 200).uniqueIndex()
    val location = varchar("location", 200)
}


class WeatherLocation(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<WeatherLocation>(WeatherLocationTable)

    var discordUserId by WeatherLocationTable.discordUserId
    var location by WeatherLocationTable.location

    override fun toString(): String {
        return "WeatherLocation(id=$id, discordUserId=$discordUserId, location=$location)"
    }
}

//fun <ID : Comparable<ID>, T: Entity<ID>> EntityClass<ID, T>.newOrUpdate(keyColumn: Column<String>, keyValue: String, init: T.() -> Unit): T {
//    return this.find(keyColumn eq keyValue).firstOrNull() ?: new(null, init)
//}





