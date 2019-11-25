package com.spencer.shoppinglist

import android.app.Dialog
import android.content.ClipData
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.spencer.shoppinglist.data.Categories
import com.spencer.shoppinglist.data.ShoppingItem
import kotlinx.android.synthetic.main.new_item_dialog.view.*

class ItemDialog : DialogFragment() {

    interface ItemHandler {
        fun itemCreated(item: ShoppingItem)
        fun itemUpdated(item: ShoppingItem)
    }

    private lateinit var itemHandler: ItemHandler

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is ItemHandler) {
            itemHandler = context
        } else {
            throw RuntimeException(
                getString(R.string.runtimeExcept)
            )
        }
    }

    private lateinit var etItemPrice: EditText
    private lateinit var etItemName: EditText
    private lateinit var etItemDescription: EditText
    private lateinit var categoriesSpinner: Spinner

    var isEditMode = false


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle(getString(R.string.newItem))

        val rootView = requireActivity().layoutInflater.inflate(
            R.layout.new_item_dialog, null
        )

        etItemPrice = rootView.etPrice
        etItemName = rootView.etName
        etItemDescription = rootView.etDescription
        categoriesSpinner = rootView.categorySpinner


        // Create an ArrayAdapter using a simple spinner layout and languages array
        val aa = ArrayAdapter(
            (context as ScrollingActivity),
            android.R.layout.simple_spinner_item,
            Categories.values()
        )
        // Set layout to use when the list of choices appear
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        categoriesSpinner.adapter = aa


        builder.setView(rootView)

        isEditMode = ((arguments != null) && arguments!!.containsKey(ScrollingActivity.KEY_TODO))

        if (isEditMode) {
            builder.setTitle(getString(R.string.editItem))
            var item: ShoppingItem =
                (arguments?.getSerializable(ScrollingActivity.KEY_TODO) as ShoppingItem)

            etItemName.setText(item.itemName)
            etItemDescription.setText(item.itemDescription)
            etItemPrice.setText(item.itemPrice.toString())
            categoriesSpinner.setSelection(item.itemCategory)
        }

        builder.setPositiveButton(getString(R.string.ok)) { dialog, witch ->
            // empty
        }

        return builder.create()
    }

    override fun onResume() {
        super.onResume()

        val positiveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            if (etItemName.text.isEmpty()) {
                etItemName.error = getString(R.string.emptyField)
            } else if (etItemPrice.text.isEmpty()) {
                etItemPrice.error = getString(R.string.emptyField)
            } else {
                if (isEditMode) {
                    handleItemEdit()

                } else {
                    handleItemCreate()

                }
                (dialog as AlertDialog).dismiss()
            }
        }
    }

    private fun handleItemCreate() {
        itemHandler.itemCreated(
            ShoppingItem(
                null,
                etItemName.text.toString(),
                etItemDescription.text.toString(),
                etItemPrice.text.toString().toFloat(),
                categoriesSpinner.selectedItemPosition,
                false

            )
        )

    }

    private fun handleItemEdit() {
        val itemToEdit = arguments?.getSerializable(
            ScrollingActivity.KEY_TODO
        ) as ShoppingItem
        itemToEdit.itemName = etItemName.text.toString()
        itemToEdit.itemDescription = etItemDescription.text.toString()
        itemToEdit.itemPrice = etItemPrice.text.toString().toFloat()
        itemToEdit.itemCategory = categoriesSpinner.selectedItemPosition

        itemHandler.itemUpdated(itemToEdit)
    }
}