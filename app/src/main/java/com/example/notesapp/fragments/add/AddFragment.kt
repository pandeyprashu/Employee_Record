package com.example.notesapp.fragments.add

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.notesapp.R
import com.example.notesapp.data.UserViewModel
import com.example.notesapp.databinding.FragmentAddBinding
import com.example.notesapp.model.User
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.ByteArrayOutputStream
import java.util.*


class AddFragment : Fragment(R.layout.fragment_add),DatePickerDialog.OnDateSetListener {

    private lateinit var mUserViewModel:UserViewModel
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!


    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var PERMISSION_ID=2
    private var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    private lateinit var imageUri:Uri
    private var photo:String?="drawable://"+R.drawable.ic_person


    private val REQUEST_PERMISSION = 100
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2

    private val calendar=Calendar.getInstance()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddBinding.bind(view)
        mUserViewModel=ViewModelProvider(this).get(UserViewModel::class.java)
        binding.btnSubmit.setOnClickListener {
            checkValidation()
            validEmail()
            validPersonalEmail()
        }

        binding.location.setOnClickListener {
            findLocation()
        }

        binding.BirthDate.setOnClickListener{
            val year=calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog=DatePickerDialog(requireContext(),
                this@AddFragment,
                year,
                month,
                day
            )


            datePickerDialog.show()




        }

        binding.imageView.setOnClickListener {
            val permissionDialog=AlertDialog.Builder(context)
            permissionDialog.setTitle("Select an action")
            val picture=arrayOf("Select Photo from Gallery",
                    "Take Picture")
            permissionDialog.setItems(picture){dialog,which->
                when(which){
                    0->openGallery()
                    1->openCamera()
                }

            }
            permissionDialog.show()

        }

        mFusedLocationClient=LocationServices.getFusedLocationProviderClient(requireContext())

        view.setOnTouchListener{_,_->
            hideKeyboard()
            false

        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(year,month,dayOfMonth)
        val date="$dayOfMonth-${month+1}-$year"
        binding.BirthDate.setText(date)
    }


    override fun onResume(){
        super.onResume()
        checkCameraPermission()
    }

    private fun checkCameraPermission() {
        if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.CAMERA)!=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
                REQUEST_PERMISSION
            )
        }
    }

    private fun openGallery() {
        val intent=Intent(Intent.ACTION_PICK)

        intent.type="image/*"
        startActivityForResult(intent,REQUEST_PICK_IMAGE)

    }

    private fun openCamera(){
        val intent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent,REQUEST_IMAGE_CAPTURE)
    }

    fun getImageUriFromBitmap(context: Context,bitmap: Bitmap): Uri{
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path.toString())
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


           if(  resultCode==Activity.RESULT_OK){

                if(requestCode== REQUEST_IMAGE_CAPTURE){
                    val bitmap=data?.extras?.get("data") as Bitmap

                    binding.imageView.load(bitmap){
                        crossfade(true)
                        crossfade(1000)
                    }

                    photo=getImageUriFromBitmap(requireContext(),bitmap).toString()

                }
                else if(requestCode==REQUEST_PICK_IMAGE){
                    val uri =data?.data
                    binding.imageView.load(uri){
                        crossfade(true)
                        crossfade(1000)
                    }
                    if (uri != null) {
                        imageUri=uri
                    }
                    photo=imageUri.toString()
                }


            }
        }





    private fun checkValidation() {
        if (binding.PersonName.text.toString().isEmpty())
            binding.PersonName.error = "It cannot be empty"



        if (binding.phone.text.toString().isEmpty())
            binding.phone.error = "It cannot be empty"


        if (binding.personalEmail.text.toString().isEmpty())
            binding.personalEmail.error = "It cannot be Empty"

        if (binding.phone.text.toString().length > 10||binding.phone.text.toString().length<10)
            binding.phone.error = "Invalid Number"


        if(binding.officialemail.text.toString().isEmpty())
            binding.officialemail.error="It cannot be Empty"



    }
    private fun validEmail() {
        if( binding.officialemail.text.toString().isEmpty()){
            binding.officialemail.error="Cannot be Empty"
        }else{
            if(binding.officialemail.text.toString().trim{it <= ' '}.matches(emailPattern.toRegex())){
               println("Hello")

            }else{


                binding.officialemail.error="Invalid Email Address"
            }
        }
    }
    private fun validPersonalEmail(){
        if(binding.personalEmail.text.toString().isEmpty()){
            binding.personalEmail.error="Cannot be Empty"
        }else{
            if(binding.personalEmail.text.toString().trim(){it<= ' '}.matches(emailPattern.toRegex())){
                insertDataToDatabase()
            }else{
                binding.personalEmail.error="Invalid Email Address"
            }
        }
    }
    private fun insertDataToDatabase() {
        binding.apply {
            val name = PersonName.text.toString()
            val mailid=officialemail.text.toString()
            val pmailid=personalEmail.text.toString()
            val designation=designation.text.toString()
            val phone=phone.text.toString()
            val address=address.text.toString()
            val total=total.text.toString()
            val years=years.text.toString()
            val organisation = organisation.text.toString()
            val date=BirthDate.text.toString()


            if(inputCheck(name,mailid,pmailid,designation,phone,address,total,years,organisation)){
                if(photo!=null){
                val user= User(0,photo!!,date,name,mailid,pmailid,phone,address,designation,total,years,organisation)

                mUserViewModel.addUser(user)
                Toast.makeText(requireContext(),"Successfully Added",Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_addFragment_to_listFragment)
                }else{
                    Toast.makeText(requireContext(),"Please Upload an image",Toast.LENGTH_SHORT).show()
                }

            }else{
                Toast.makeText(requireContext(),"Please fill out all fields",Toast.LENGTH_SHORT).show()
            }

        }


    }

    private fun inputCheck(name:String,id:String,mid:String,des:String,
    phone:String,address:String,total:String,years:String,org:String):Boolean {

        return !(TextUtils.isEmpty(name)||TextUtils.isEmpty(id)||TextUtils.isEmpty(mid)
                ||TextUtils.isEmpty(des)||TextUtils.isEmpty(address)||
                TextUtils.isEmpty(total)||TextUtils.isEmpty(years)||TextUtils.isEmpty(org)
                ||TextUtils.isEmpty(phone))
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermission():Boolean {
        if(
            ActivityCompat.checkSelfPermission
                (requireContext(),Manifest.permission.ACCESS_FINE_LOCATION)==
            PackageManager.PERMISSION_GRANTED||
            ActivityCompat.checkSelfPermission
                (requireContext(),Manifest.permission.ACCESS_COARSE_LOCATION)==
            PackageManager.PERMISSION_GRANTED){
            return true
        }

        return false
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION),PERMISSION_ID)
       findLocation()
    }



    private fun findLocation(){
        if(checkPermission()){
            if(isLocationEnabled()){
                mFusedLocationClient.lastLocation.addOnCompleteListener{task->
                    val location:Location?=task.result
                    if(location!=null){
                        val geocoder=Geocoder(requireContext(), Locale.getDefault())
                        var list:MutableList<Address>? =
                            geocoder.getFromLocation(location.latitude,location.longitude,1)

                        binding.address.setText(list!![0].getAddressLine(0) )
                    }



                }
            }else{
                Toast.makeText(requireContext(),"Please Turn on Location ",Toast.LENGTH_SHORT).show()

            }

        }else{
            requestPermission()
        }
    }

    private fun hideKeyboard() {
       val imm=requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken,0)
    }

    override fun onDestroyView() {
        view?.setOnTouchListener(null)
        super.onDestroyView()
    }


}



