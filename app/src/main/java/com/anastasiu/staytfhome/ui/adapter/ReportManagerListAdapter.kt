package com.anastasiu.staytfhome.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anastasiu.staytfhome.R
import com.anastasiu.staytfhome.data.model.Report
import com.anastasiu.staytfhome.data.model.User
import com.anastasiu.staytfhome.ui.event.Event
import com.anastasiu.staytfhome.ui.fragment.ReportManagerFragment

class ReportManagerListAdapter(context: Context, private val reportManagerFragment: ReportManagerFragment)
    : RecyclerView.Adapter<ReportManagerListAdapter.ReportViewHolder>() {
    var reports = mutableListOf<Report>()
    lateinit var user: User

    val layoutInflater = LayoutInflater.from(context)

    inner class ReportViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvSymptoms: TextView = itemView.findViewById(R.id.tvReportSymptoms)
        val tvProximity: TextView = itemView.findViewById(R.id.tvReportProximity)
        val tvComment: TextView = itemView.findViewById(R.id.tvReportComment)
        val tvTestStatus: TextView = itemView.findViewById(R.id.tvReportTestStatus)
        val llReport: LinearLayout = itemView.findViewById(R.id.llReport)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val ihTrip = layoutInflater.inflate(R.layout.report_item_holder, parent, false)
        val viewHolder = ReportViewHolder(ihTrip)

        return viewHolder
    }

    override fun getItemCount(): Int {
        return reports.size
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        // TODO sort by proximity
        val current = reports[position]
        holder.apply {
            tvSymptoms.text = current.symptoms
                .joinToString(", ")
                .replace("_", " ")
                .capitalize()

            tvProximity.text = current.longitude.toString() // TODO show distance in meters
            tvProximity.visibility = View.GONE

            if (current.comment.isNullOrEmpty()) {
                tvComment.visibility = View.GONE
            } else {
                tvComment.visibility = View.VISIBLE
                tvComment.text = current.comment.take(10) + if (current.comment.length > 10) "..." else ""
            }

            tvTestStatus.text = "Covid-19 " + when (current.testStatus) {
                "0" -> "Negative"
                "1" -> "Positive"
                else -> "Not Tested"
            }
        }
    }
}
