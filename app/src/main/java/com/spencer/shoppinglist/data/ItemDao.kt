package com.spencer.shoppinglist.data

import androidx.room.*

@Dao
interface ItemDao {

    @Query("SELECT * FROM items")
    fun getAllTodo(): List<ShoppingItem>

    @Insert
    fun insertItem(item: ShoppingItem): Long

    @Delete
    fun deleteTodo(item: ShoppingItem)

    @Update
    fun updateItem(item: ShoppingItem)

    @Query("DELETE FROM items")
    fun deleteAllTodo()

    @Query("SELECT * FROM items WHERE category = :category")
    fun getCategory(category: Int): List<ShoppingItem>

    @Query("SELECT SUM(price) FROM items")
    fun getPriceCount() : Float

}