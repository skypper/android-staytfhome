package com.anastasiu.staytfhome.ui.module

import android.app.Application
import com.anastasiu.staytfhome.ui.adapter.ReportManagerListAdapter
import com.anastasiu.staytfhome.ui.fragment.ReportManagerFragment
import dagger.Module
import dagger.Provides

@Module
class ReportManagerFragmentModule {
    @Provides
    fun providesReportManagerListAdapter(application: Application, reportManagerFragment: ReportManagerFragment)
            = ReportManagerListAdapter(application, reportManagerFragment)
}