package com.spencer.shoppinglist

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.spencer.shoppinglist.adapter.ItemAdapter
import com.spencer.shoppinglist.data.AppDatabase
import com.spencer.shoppinglist.data.Categories
import com.spencer.shoppinglist.data.ShoppingItem
import kotlinx.android.synthetic.main.activity_scrolling.*
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt

class ScrollingActivity : AppCompatActivity(), ItemDialog.ItemHandler,
    AdapterView.OnItemSelectedListener {

    companion object {
        const val KEY_TODO = "KEY_TODO"
        const val KEY_STARTED = "KEY_STARTED"
        const val TAG_TODO_DIALOG = "TAG_ITEM_DIALOG"
        const val TAG_TODO_EDIT = "TAG_ITEM_EDIT"
    }

    lateinit var itemAdapter: ItemAdapter
    private lateinit var categoryDisplaySpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)

        setSupportActionBar(toolbar)

        initRecyclerView()


        fab.setOnClickListener {
            showAddItemDialog()
        }

        fabDeleteAll.setOnClickListener {
            itemAdapter.deleteAllTodos()
        }

        if (!wasStartedBefore()) {
            MaterialTapTargetPrompt.Builder(this)
                .setTarget(R.id.fab)
                .setPrimaryText(R.string.newItem)
                .setSecondaryText(getString(R.string.newItemClick))
                .show()
            saveWasStarted()
        }


        // category spinner create

        categoryDisplaySpinner = categorySpinner

        setSpinner()


    }

    private fun setSpinner() {
        // Create an ArrayAdapter using a simple spinner layout and languages array

        val aa = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            Categories.values()
        )
        // Set layout to use when the list of choices appear
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        categoryDisplaySpinner.adapter = aa

        categoryDisplaySpinner!!.setOnItemSelectedListener(this)


    }

    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        initRecyclerViewWithCategory(Categories.values()[position])
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {

    }


    fun saveWasStarted() {
        var sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        var editor = sharedPref.edit()
        editor.putBoolean(KEY_STARTED, true)
        editor.apply()
    }

    fun wasStartedBefore(): Boolean {
        var sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        return sharedPref.getBoolean(KEY_STARTED, false)
    }

    private fun initRecyclerViewWithCategory(category: Categories) {
        if (category == Categories.ANY) {
            initRecyclerView()
        } else {
            Thread {
                var todoList =
                    AppDatabase.getInstance(this@ScrollingActivity).itemDao()
                        .getCategory(category = category.ordinal)

                runOnUiThread {
                    itemAdapter = ItemAdapter(this, todoList)
                    recyclerTodo.adapter = itemAdapter

                    var itemDecoration = DividerItemDecoration(
                        this,
                        DividerItemDecoration.VERTICAL
                    )
                    recyclerTodo.addItemDecoration(itemDecoration)
                }
            }.start()
        }
    }

    private fun initRecyclerView() {
        Thread {
            var todoList =
                AppDatabase.getInstance(this@ScrollingActivity).itemDao().getAllTodo()

            var price = AppDatabase.getInstance(this@ScrollingActivity).itemDao().getPriceCount()

            runOnUiThread {
                itemAdapter = ItemAdapter(this, todoList)
                recyclerTodo.adapter = itemAdapter

                var itemDecoration = DividerItemDecoration(
                    this,
                    DividerItemDecoration.VERTICAL
                )
                recyclerTodo.addItemDecoration(itemDecoration)
                tvPriceCount.text = price.toString()

            }
        }.start()
    }

    fun showAddItemDialog() {
        ItemDialog().show(
            supportFragmentManager,
            TAG_TODO_DIALOG
        )
    }

    var editIndex: Int = -1

    fun showEditItemDialog(itemToEdit: ShoppingItem, idx: Int) {
        editIndex = idx

        val editDialog = ItemDialog()

        val bundle = Bundle()

        bundle.putSerializable(KEY_TODO, itemToEdit)

        editDialog.arguments = bundle

        editDialog.show(
            supportFragmentManager,
            TAG_TODO_EDIT
        )
    }

    fun showInfoDialog(itemToEdit: ShoppingItem, idx: Int) {
        editIndex = idx

        val editDialog = DescriptionDialog()

        val bundle = Bundle()

        bundle.putSerializable(KEY_TODO, itemToEdit)

        editDialog.arguments = bundle

        editDialog.show(
            supportFragmentManager,
            TAG_TODO_EDIT
        )
    }

    fun saveItem(item: ShoppingItem) {
        Thread {
            var newId = AppDatabase.getInstance(this).itemDao().insertItem(
                item
            )
            var price = AppDatabase.getInstance(this).itemDao().getPriceCount()

            item.itemId = newId

            runOnUiThread {
                itemAdapter.addItem(item)
                tvPriceCount.text = price.toString()
            }
        }.start()
    }

    override fun itemCreated(item: ShoppingItem) {
        saveItem(item)
    }

    override fun itemUpdated(item: ShoppingItem) {
        Thread {
            AppDatabase.getInstance(this@ScrollingActivity).itemDao().updateItem(item)
            var price = AppDatabase.getInstance(this).itemDao().getPriceCount()

            runOnUiThread {
                itemAdapter.updateItemOnPosition(item, editIndex)
                tvPriceCount.text = price.toString()
            }
        }.start()
    }
}
