package com.example.android.politicalpreparedness.representative

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R.string.location_on_required
import com.example.android.politicalpreparedness.R.string.location_required
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import java.util.Locale

class DetailFragment : Fragment(), OnSuccessListener<Location> {

    companion object {
        private val TAG = DetailFragment::class.java.simpleName
        private const val REQUEST_LOCATION_PERMISSION = 1
        private const val REQUEST_TURN_DEVICE_LOCATION_ON = 2
    }

    private lateinit var representativeViewModel: RepresentativeViewModel

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var binding: FragmentRepresentativeBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        initViewModel()

        //TODO: Establish bindings
        binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.lifecycleOwner = this

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        //TODO: Define and assign Representative adapter
        binding.representativesRecycler.adapter = RepresentativeListAdapter()

        //TODO: Populate Representative adapter

        //TODO: Establish button listeners for field and location search
        binding.buttonLocation.setOnClickListener {
            onUseMyLocationButtonClicked()
        }

        return binding.root
    }

    private fun initViewModel() {
        representativeViewModel = ViewModelProvider(this).get(RepresentativeViewModel::class.java)
    }

    private fun onUseMyLocationButtonClicked() {
        if (isPermissionGranted()) {
            verifyUserHasLocationEnabled()
        }
        else {
            requestLocationPermission()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PERMISSION_GRANTED)) {
                verifyUserHasLocationEnabled()
            }
            else {
                toast(getString(location_required))
            }
        }
    }

    private fun verifyUserHasLocationEnabled(resolve:Boolean = true) {
        val locationSettingsResponseTask = getLocationSettingsResponseTask()

        locationSettingsResponseTask.addOnFailureListener { exception ->
            onLocationSettingsResponseError(exception, resolve)
        }

        locationSettingsResponseTask.addOnCompleteListener(this::onLocationSettingsResponseSuccess)
    }

    private fun getLocationSettingsResponseTask() : Task<LocationSettingsResponse> {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(getLocationRequest())

        val settingsClient = LocationServices.getSettingsClient(activity!!)
        return settingsClient.checkLocationSettings(builder.build())
    }

    private fun getLocationRequest() : LocationRequest {
        return LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
    }

    private fun onLocationSettingsResponseError(exception: Exception, resolve:Boolean = true) {
        if (exception is ResolvableApiException && resolve){
            try {
                toast("Trying to resolve!")
                exception.startResolutionForResult(activity, REQUEST_TURN_DEVICE_LOCATION_ON)
            }
            catch (sendEx: IntentSender.SendIntentException) {
                toast("Exception while trying to resolve")
                Log.d(TAG, "Error getting location settings resolution: " + sendEx.message)
            }
        }
        else {
            toast(getString(location_on_required))
        }
    }

    private fun onLocationSettingsResponseSuccess(task : Task<LocationSettingsResponse>) {
        if ( task.isSuccessful ) {
            toast("Location is on!")
            getLocation()
        }
        else {
            toast("task failed")
        }
    }

    private fun requestLocationPermission() {
        requestPermissions(arrayOf(ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
    }

    private fun isPermissionGranted() : Boolean {
        return checkSelfPermission(context!!, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity(), this)
    }

    override fun onSuccess(location: Location?) {
        if (location != null) {
            val address = geoCodeLocation(location)
            updateFieldsBasedOnAddress(address)
        }
        else {
            toast("Unable to acquire last known location")
        }
    }

    private fun updateFieldsBasedOnAddress(address: Address) {
        binding.address = address
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    private fun toast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

}