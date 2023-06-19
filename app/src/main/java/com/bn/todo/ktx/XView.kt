package com.bn.todo.ktx

import android.graphics.Paint
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bn.todo.util.DialogUtil

fun Fragment.showDialog(titleStringId: Int? = null, messageStringId: Int) =
    DialogUtil.showDialog(
        requireActivity(),
        titleStringId?.let { getString(it) },
        getString(messageStringId)
    )

fun Fragment.showDialog(title: String? = null, message: String) =
    DialogUtil.showDialog(requireActivity(), title, message)

fun AppCompatActivity.showDialog(title: String? = null, message: String) =
    DialogUtil.showDialog(this, title, message)

fun Fragment.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(requireContext(), message, duration).show()

fun DrawerLayout.openDrawer() = this.openDrawer(GravityCompat.START)

fun DrawerLayout.closeDrawer() = this.closeDrawer(GravityCompat.START)

fun DrawerLayout.setLockState(locked: Boolean) =
    this.setDrawerLockMode(
        if (locked)
            DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        else DrawerLayout.LOCK_MODE_UNDEFINED
    )

fun View.setVisible() {
    visibility = View.VISIBLE
}

fun View.setInvisible() {
    visibility = View.INVISIBLE
}

fun View.setGone() {
    visibility = View.GONE
}

fun TextView.setStrikeThrough() {
    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
}

fun TextView.unsetStrikeThrough() {
    paintFlags = paintFlags xor Paint.STRIKE_THRU_TEXT_FLAG
}