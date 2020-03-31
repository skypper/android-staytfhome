package com.anastasiu.staytfhome.ui.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.anastasiu.staytfhome.R
import com.anastasiu.staytfhome.data.forms.ReportForm
import com.anastasiu.staytfhome.databinding.ReportItemFormBinding
import com.anastasiu.staytfhome.ui.activity.MainActivity
import com.anastasiu.staytfhome.ui.event.Event
import com.anastasiu.staytfhome.ui.viewmodel.ReportManagerViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.report_item_form.*
import javax.inject.Inject

class ReportCreateUpdateFragment : Fragment() {
    private lateinit var binding: ReportItemFormBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var reportManagerViewModel: ReportManagerViewModel

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.report_item_form, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reportManagerViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(ReportManagerViewModel::class.java)

        binding.model = reportManagerViewModel
        binding.lifecycleOwner = this

        if (reportManagerViewModel.reportForm.id != null) {
            btReportSubmit.text = "Update"
        }

        reportManagerViewModel.reportCreateSubmitFormEvent.observe(this, Observer<Event<ReportForm>> { event ->
            event.getContentIfNotHandledOrReturnNull()?.let {
                Toast.makeText(context, "A new report has been successfully created.", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        })
        reportManagerViewModel.reportUpdateSubmitFormEvent.observe(this, Observer<Event<ReportForm>> { event ->
            event.getContentIfNotHandledOrReturnNull()?.let {
                findNavController().popBackStack()
            }
        })

        getLocation()
    }

    private fun getLocation(): Boolean {
        val mLocationManager: LocationManager =
            activity!!.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                MainActivity.MY_PERMISSIONS_REQUEST_READ_CONTACTS
            )
            return true
        }

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2f, object :
            LocationListener {
            override fun onLocationChanged(location: Location?) {
                Log.e("location manager", "onLocationChanged $location")
                reportManagerViewModel.reportForm.apply {
                    latitude = location?.latitude
                    longitude = location?.longitude
                }
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                Log.e("location manager", "onStatusChanged")
            }

            override fun onProviderEnabled(provider: String?) {
                Log.e("location manager", "onProviderEnabled")
            }

            override fun onProviderDisabled(provider: String?) {
                Log.e("location manager", "onProviderDisabled")
            }
        })

        val location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        Log.e("getLocation", "The location is $location.")
        reportManagerViewModel.reportForm.apply {
            latitude = location.latitude
            longitude = location.longitude
        }

        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MainActivity.MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.e("onRequestPermissionsResult", "Permission granted")
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.e("onRequestPermissionsResult", "Permission NOT granted")
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        reportManagerViewModel.reportForm.clearInput()
    }
}
