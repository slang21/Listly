package com.spencer.shoppinglist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.spencer.shoppinglist.R
import com.spencer.shoppinglist.ScrollingActivity
import com.spencer.shoppinglist.data.AppDatabase
import com.spencer.shoppinglist.data.Categories
import com.spencer.shoppinglist.data.ShoppingItem
import kotlinx.android.synthetic.main.activity_scrolling.*
import kotlinx.android.synthetic.main.activity_scrolling.view.*
import kotlinx.android.synthetic.main.list_row.view.*
import java.util.*


class ItemAdapter : RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    var itemList = mutableListOf<ShoppingItem>()

    val context: Context

    constructor(context: Context, listTodos: List<ShoppingItem>) {
        this.context = context

        itemList.addAll(listTodos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val todoRow = LayoutInflater.from(context).inflate(
            R.layout.list_row, parent, false
        )
        return ViewHolder(todoRow)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = itemList.get(holder.adapterPosition)

        holder.tvName.text = item.itemName
        holder.cbTodo.isChecked = item.done
        holder.tvPrice.text = "$ ${item.itemPrice.toString()}"

        holder.btnDelete.setOnClickListener {
            deleteTodo(holder.adapterPosition)
        }

        holder.cbTodo.setOnClickListener {
            item.done = holder.cbTodo.isChecked
            updateTodo(item)
        }

        holder.btnEdit.setOnClickListener {
            (context as ScrollingActivity).showEditItemDialog(
                item, holder.adapterPosition
            )
        }

        holder.btnInfo.setOnClickListener {
            (context as ScrollingActivity).showInfoDialog(
                item, holder.adapterPosition
            )
        }

        when (item.itemCategory) {
            Categories.PETS.ordinal -> holder.ivCategory.setImageResource(R.drawable.ic_pets)
            Categories.FOOD.ordinal -> holder.ivCategory.setImageResource(R.drawable.ic_restaurant_menu)
            Categories.CLOTHES.ordinal -> holder.ivCategory.setImageResource(R.drawable.ic_action_tshirt)
        }

    }

    fun updateTodo(item: ShoppingItem) {
        Thread {
            AppDatabase.getInstance(context).itemDao().updateItem(item)
            var price = AppDatabase.getInstance(context).itemDao().getPriceCount()
            (context as ScrollingActivity).runOnUiThread {
                context.tvPriceCount.text = AppDatabase.getInstance(context).itemDao().getPriceCount().toString()
            }
        }.start()
    }

    fun updateItemOnPosition(item: ShoppingItem, index: Int) {
        itemList.set(index, item)
        notifyItemChanged(index)
        Thread {
            var price = AppDatabase.getInstance(context).itemDao().getPriceCount()
            (context as ScrollingActivity).runOnUiThread {
                context.tvPriceCount.text = price.toString()
            }
        }

    }

    fun deleteTodo(index: Int) {
        Thread {
            AppDatabase.getInstance(context).itemDao().deleteTodo(itemList[index])
            var price = AppDatabase.getInstance(context).itemDao().getPriceCount()
            (context as ScrollingActivity).runOnUiThread {
                itemList.removeAt(index)
                notifyItemRemoved(index)
                context.tvPriceCount.text = price.toString()
            }
        }.start()
    }

    fun addItem(item: ShoppingItem) {
        itemList.add(item)
        notifyItemInserted(itemList.lastIndex)
    }

    fun deleteAllTodos() {
        Thread {
            AppDatabase.getInstance(context).itemDao().deleteAllTodo()
            var price = AppDatabase.getInstance(context).itemDao().getPriceCount()

            (context as ScrollingActivity).runOnUiThread {
                itemList.clear()
                notifyDataSetChanged()
                context.tvPriceCount.text = price.toString()
            }
        }.start()
    }


    fun onDismissed(position: Int) {
        deleteTodo(position)
    }

    fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(itemList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivCategory = itemView.ivCategory
        val tvName = itemView.tvName
        val cbTodo = itemView.cbTodo
        val tvPrice = itemView.tvPrice
        val btnDelete = itemView.btnDelete
        val btnEdit = itemView.btnEdit
        val btnInfo = itemView.btnInfo
    }

}