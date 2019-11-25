package com.spencer.shoppinglist.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "items")
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true) var itemId: Long?,
    @ColumnInfo(name = "name") var itemName: String,
    @ColumnInfo(name = "description") var itemDescription: String,
    @ColumnInfo(name = "price") var itemPrice: Float,
    @ColumnInfo(name = "category") var itemCategory: Int,
    @ColumnInfo(name = "bought") var done: Boolean
) : Serializable