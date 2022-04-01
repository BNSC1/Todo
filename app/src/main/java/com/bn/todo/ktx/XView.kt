package com.bn.todo.ktx

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
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

private fun AppCompatActivity.startActivity(activity: Class<AppCompatActivity>) {
    val intent = Intent(this, activity)
    startActivity(intent)
    finish()
}

fun Fragment.showToast(messageStringId: Int, duration: Int = Toast.LENGTH_SHORT) =
    showToast(getString(messageStringId), duration)

fun Fragment.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(requireActivity(), message, duration).show()

fun DrawerLayout.openDrawer() = this.openDrawer(GravityCompat.START)

fun DrawerLayout.closeDrawer() = this.closeDrawer(GravityCompat.START)

fun DrawerLayout.setLocked(locked: Boolean) =
    this.setDrawerLockMode(
        if (locked)
            DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        else DrawerLayout.LOCK_MODE_UNDEFINED
    )

fun RecyclerView.addItemDecoration(dividerResId: Int, theme: Resources.Theme? = null) =
    this.addItemDecoration(
        DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL).apply {
            setDrawable(ResourcesCompat.getDrawable(resources, dividerResId, theme)!!)
        })

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeInVisible() {
    visibility = View.INVISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}

internal val Context.layoutInflater: LayoutInflater
    get() = LayoutInflater.from(this)
