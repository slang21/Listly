package com.spencer.shoppinglist

import android.app.Dialog
import android.content.ClipData
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.spencer.shoppinglist.data.Categories
import com.spencer.shoppinglist.data.ShoppingItem
import kotlinx.android.synthetic.main.item_description_dialog.view.*
import kotlinx.android.synthetic.main.new_item_dialog.view.*
import kotlinx.android.synthetic.main.new_item_dialog.view.etDescription
import kotlinx.android.synthetic.main.new_item_dialog.view.etName
import org.w3c.dom.Text

class DescriptionDialog : DialogFragment() {
    private lateinit var tvItemPrice: TextView
    private lateinit var tvItemName: TextView
    private lateinit var tvItemDescription: TextView
//    private lateinit var categoriesSpinner: TextView

    var isEditMode = false


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        val rootView = requireActivity().layoutInflater.inflate(
            R.layout.item_description_dialog, null
        )

        val shoppingItem = arguments?.getSerializable(
            ScrollingActivity.KEY_TODO
        ) as ShoppingItem

        tvItemPrice = rootView.tvPrice
        tvItemDescription = rootView.tvDescription
//        categoriesSpinner = rootView.tvCategory

        builder.setTitle(shoppingItem.itemName)


        builder.setView(rootView)

        tvItemDescription.setText(shoppingItem.itemDescription)
        tvItemPrice.setText(shoppingItem.itemPrice.toString())

        builder.setPositiveButton(getString(R.string.ok)) { dialog, witch ->
            // empty
        }

        return builder.create()
    }

    override fun onResume() {
        super.onResume()

        val positiveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            (dialog as AlertDialog).dismiss()
        }
    }
}