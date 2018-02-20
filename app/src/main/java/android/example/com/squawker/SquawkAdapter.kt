/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package android.example.com.squawker

import android.database.Cursor
import android.example.com.squawker.provider.SquawkContract
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_squawk_list.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Converts cursor data for squawk messages into visible list items in a RecyclerView
 */
class SquawkAdapter : RecyclerView.Adapter<SquawkAdapter.SquawkViewHolder>() {

    companion object {
        private val sDateFormat = SimpleDateFormat("dd MMM")
        private val MINUTE_MILLIS = (1000 * 60).toLong()
        private val HOUR_MILLIS = 60 * MINUTE_MILLIS
        private val DAY_MILLIS = 24 * HOUR_MILLIS
    }

    private var mData: Cursor? = null

//Overridden functions
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SquawkViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_squawk_list, parent, false)

        return SquawkViewHolder(v)
    }

    override fun onBindViewHolder(holder: SquawkViewHolder, position: Int) {
        if (mData != null) {
            mData!!.moveToPosition(position)

            val message = mData!!.getString(MainActivity.COL_NUM_MESSAGE)
            val author = mData!!.getString(MainActivity.COL_NUM_AUTHOR)
            val authorKey = mData!!.getString(MainActivity.COL_NUM_AUTHOR_KEY)

            var date = createReadableDate(dateInMilliseconds = mData!!.getLong(MainActivity.COL_NUM_DATE))
            // Add a dot to the date string
            date = "\u2022 " + date

            setSquawkTextViews(holder, message, author, date)
            setAuthorImage(authorKey, holder)
        }
    }



    override fun getItemCount(): Int {
        return if (null == mData) 0 else mData!!.count
    }

    fun swapCursor(newCursor: Cursor?) {
        mData = newCursor
        notifyDataSetChanged()
    }

    inner class SquawkViewHolder(layoutView: View) : RecyclerView.ViewHolder(layoutView) {
        val authorTextView = layoutView.author_text_view
        val messageTextView = layoutView.message_text_view
        val dateTextView = layoutView.date_text_view
        val authorImageView = layoutView.author_image_view
    }

//2nd Layer Functions

    // Change how the date is displayed depending on whether it was written in the last minute,
    // the hour, etc.
    private fun createReadableDate(dateInMilliseconds: Long): String {
        val now = System.currentTimeMillis()
        return if (now - dateInMilliseconds< DAY_MILLIS) {
            if (now - dateInMilliseconds < HOUR_MILLIS) {
                val minutes = Math.round(((now - dateInMilliseconds) / MINUTE_MILLIS).toFloat()).toLong()
                minutes.toString() + "m"
            } else {
                val hours = Math.round(((now - dateInMilliseconds) / HOUR_MILLIS).toFloat()).toLong()
                hours.toString() + "h"
            }
        } else {
            sDateFormat.format(Date(dateInMilliseconds))
        }
    }

    private fun setAuthorImage(authorKey: String?, holder: SquawkViewHolder) {
        when (authorKey) {
            SquawkContract.ASSER_KEY -> holder.authorImageView.setImageResource(R.drawable.asser)
            SquawkContract.CEZANNE_KEY -> holder.authorImageView.setImageResource(R.drawable.cezanne)
            SquawkContract.JLIN_KEY -> holder.authorImageView.setImageResource(R.drawable.jlin)
            SquawkContract.LYLA_KEY -> holder.authorImageView.setImageResource(R.drawable.lyla)
            SquawkContract.NIKITA_KEY -> holder.authorImageView.setImageResource(R.drawable.nikita)
            else -> holder.authorImageView.setImageResource(R.drawable.test)
        }
    }

    private fun setSquawkTextViews(holder: SquawkViewHolder, message: String?, author: String?, date: String) {
        holder.messageTextView.text = message
        holder.authorTextView.text = author
        holder.dateTextView.text = date
    }


}
