package com.anastasiu.staytfhome.ui.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.anastasiu.staytfhome.R
import com.anastasiu.staytfhome.data.model.Report
import com.anastasiu.staytfhome.data.model.User
import com.anastasiu.staytfhome.ui.adapter.ReportManagerListAdapter
import com.anastasiu.staytfhome.ui.viewmodel.LoginViewModel
import com.anastasiu.staytfhome.ui.viewmodel.ReportManagerViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_report_manager.*
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ReportManagerFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: Factory

    lateinit var reportManagerViewModel: ReportManagerViewModel
    lateinit var loginViewModel: LoginViewModel

    @Inject
    lateinit var reportManagerListAdapter: ReportManagerListAdapter

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report_manager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reportManagerViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(ReportManagerViewModel::class.java)
        loginViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(LoginViewModel::class.java)

        showLoading(true)
        loginViewModel.user.observe(this, Observer<User> { _user ->
            if (_user != null) {
                reportManagerListAdapter.user = _user
                setupRecyclerView()
                observeAndLoadReportContents(_user)
            }
        })
    }

    private fun showLoading(enabled: Boolean) {
        rvReports.visibility = if (enabled) View.GONE else View.VISIBLE
        swipeRefresh.isRefreshing = enabled
    }

    private fun setupRecyclerView() {
        val viewManager = LinearLayoutManager(activity)
        rvReports.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = reportManagerListAdapter
        }

        swipeRefresh.setOnRefreshListener {
            reportManagerViewModel.fetchReports()
        }
        reportManagerViewModel.reports.observe(this, Observer<List<Report>> { _reports ->
            val reports = _reports.toMutableList()
            reports.removeIf { report -> report.userId != reportManagerListAdapter.user.id }
            reportManagerListAdapter.reports = reports
            reportManagerListAdapter.notifyDataSetChanged()
        })
    }

    private fun observeAndLoadReportContents(user: User) {
        reportManagerViewModel.reports.observe(this, Observer<List<Report>> { _reports ->
            val myReport = _reports.find { report -> report.userId == user.id }
            if (myReport != null) {
                reportCard.visibility = View.VISIBLE

                val symptomsColor = when (myReport.symptoms.size) {
                    3 -> Color.CYAN
                    5 -> Color.RED
                    else -> Color.BLACK
                }
                tvMyReportSymptoms.text = myReport.symptoms
                    .joinToString(", ")
                    .replace("_", " ")
                    .capitalize()
                tvMyReportSymptoms.setTextColor(symptomsColor)

                if (myReport.comment.isNullOrEmpty()) {
                    tvMyReportComment.visibility = View.GONE
                } else {
                    tvMyReportComment.visibility = View.VISIBLE
                    tvMyReportComment.text = myReport.comment
                }

                val testStatusEnglish = when (myReport.testStatus) {
                    "0" -> "Negative"
                    "1" -> "Positive"
                    else -> "Not Tested"
                }
                val testStatusColor = when(myReport.testStatus) {
                    "0" -> Color.GREEN
                    "1" -> Color.RED
                    else -> Color.BLACK
                }
                tvMyReportTestStatus.text = testStatusEnglish
                tvMyReportTestStatus.setTextColor(testStatusColor)

                tvMyReportDateTime.text = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                    .format(myReport.dateTime).replace("T", " ").dropLast(4)
            } else {
                reportCard.visibility = View.GONE
            }

            reportManagerListAdapter.apply {
                reports = _reports.toMutableList()
                notifyDataSetChanged()
            }
            showLoading(false)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.report_manager_action_bar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.actionReportCreate -> {
            val myReport = reportManagerViewModel.reports.value
                ?.find { report -> report.userId == loginViewModel.user.value?.id }
            if (myReport != null) {
                reportManagerViewModel.reportForm.apply {
                    id = myReport.id
                    hasCough = myReport.symptoms.contains("cough")
                    hasFever = myReport.symptoms.contains("fever")
                    hasTiredness = myReport.symptoms.contains("tiredness")
                    hasDifficultyBreathing = myReport.symptoms.contains("difficulty_breathing")
                    latitude = myReport.latitude
                    longitude = myReport.longitude
                    comment = myReport.comment
                    testStatus = when (myReport.testStatus) {
                        "0" -> R.id.rbTestStatusNegative
                        "1" -> R.id.rbTestStatusPositive
                        else -> R.id.rbTestStatusNotTested
                    }
                    userId = myReport.userId
                }
            }
            findNavController().navigate(ReportManagerFragmentDirections.actionReportManagerFragmentToReportCreateUpdateFragment())
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}
