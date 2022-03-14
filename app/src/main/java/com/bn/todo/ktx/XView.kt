package com.bn.todo.ktx

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

fun AppCompatActivity.showDialog(titleStringId: Int? = null, messageStringId: Int) =
    DialogUtil.showDialog(
        this,
        titleStringId?.let { getString(it) },
        getString(messageStringId)
    )

fun AppCompatActivity.showDialog(title: String? = null, message: String) =
    DialogUtil.showDialog(this, title, message)

fun AppCompatActivity.showToast(messageStringId: Int, duration: Int = Toast.LENGTH_SHORT) =
    showToast(getString(messageStringId), duration)

fun AppCompatActivity.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, message, duration).show()

fun Fragment.showToast(messageStringId: Int, duration: Int = Toast.LENGTH_SHORT) =
    showToast(getString(messageStringId), duration)

fun Fragment.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(requireActivity(), message, duration).show()

fun DrawerLayout.openDrawer() = this.openDrawer(GravityCompat.START)

fun DrawerLayout.closeDrawer() = this.closeDrawer(GravityCompat.START)

fun DrawerLayout.setLocked(locked: Boolean) =
    this.setDrawerLockMode(
        if (locked)
            DrawerLayout.LOCK_MODE_UNDEFINED
        else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
    )