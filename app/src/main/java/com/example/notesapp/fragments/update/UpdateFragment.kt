package com.example.notesapp.fragments.update

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
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.bumptech.glide.Glide
import com.example.notesapp.R
import com.example.notesapp.data.UserViewModel
import com.example.notesapp.databinding.FragmentUpdateBinding
import com.example.notesapp.model.User
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.ByteArrayOutputStream
import java.util.*


class UpdateFragment : Fragment(R.layout.fragment_update),DatePickerDialog.OnDateSetListener {

    private val args by navArgs<UpdateFragmentArgs>()

    private lateinit var mUserViewModel: UserViewModel

    private var _binding:FragmentUpdateBinding?=null
    private val binding get()=_binding!!

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var PERMISSION_ID=2
    private var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
   private lateinit var imageUri: Uri

    private var photo:String?=null

    private val REQUEST_PERMISSION = 100
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2


    private val calendar=Calendar.getInstance()





    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentUpdateBinding.bind(view)

        mFusedLocationClient= LocationServices.getFusedLocationProviderClient(requireContext())

        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        binding.apply {

            photo=args.currentUser.image


            if(args.currentUser.image.equals("drawable://"+R.drawable.ic_person)){
                Glide.with(requireContext())
                    .load(R.drawable.ic_person)
                    .into(updateImageView)


            }else {

                Glide.with(requireContext())
                    .load(args.currentUser.image)
                    .into(updateImageView)
            }

            imageUri=Uri.parse(args.currentUser.image)
            updatePersonName.setText(args.currentUser.Name)
            updatePhone.setText(args.currentUser.Phone)
            updatePersonalemaillid.setText(args.currentUser.PersonalEmail)
            updateOfficialemaillid.setText(args.currentUser.OfficialEmail)
            updateBirth.setText(args.currentUser.birth)
            updateAddress.setText(args.currentUser.Address)
            updateDesignation.setText(args.currentUser.Designation)
            updateTotal.setText(args.currentUser.Experience)
            updateYears.setText(args.currentUser.Years)
            updateOrganisation.setText(args.currentUser.CurrentOrganisation)

            btnUpdate.setOnClickListener{

                validation()

            }


            btnLocation.setOnClickListener {
                fetchLocation()
            }

            updateBirth.setOnClickListener {
                val year=calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog=DatePickerDialog(requireContext(),
                    this@UpdateFragment,
                    year,
                    month,
                    day
                )


                datePickerDialog.show()

            }

            binding.updateImageView.setOnClickListener {
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

            view.setOnTouchListener{_,_->
                hideKeyboard()
                false

            }



        }
        setHasOptionsMenu(true)

    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(year,month,dayOfMonth)
        val date="$dayOfMonth-${month+1}-$year"
        binding.updateBirth.setText(date)
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

                binding.updateImageView.load(bitmap){
                    crossfade(true)
                    crossfade(1000)
                }

                photo=getImageUriFromBitmap(requireContext(),bitmap).toString()

            }
            else if(requestCode==REQUEST_PICK_IMAGE){
                val uri =data?.data
                binding.updateImageView.load(uri){
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





    private fun validation(){
        if(binding.updatePersonName.text.toString().isEmpty())
            binding.updatePersonName.error="It cannot be empty"

        if(binding.updatePersonalemaillid.text.toString().isEmpty())
            binding.updatePersonalemaillid.error="It cannot be empty"

        if(binding.updatePhone.text.toString().isEmpty())
            binding.updatePhone.error="It cannot be empty"

        if(binding.updatePhone.text.toString().length>10||binding.updatePhone.text.toString().length<10)
            binding.updatePhone.error="Invalid Number"
        else
            checkEmail()

        if(binding.updateAddress.text.toString().isEmpty())
            binding.updateAddress.error="It cannot be empty"

        if(binding.updateDesignation.text.toString().isEmpty())
            binding.updateDesignation.error="It cannot be empty"

        if(binding.updateTotal.text.toString().isEmpty())
            binding.updateTotal.error="It cannot be empty"

        if(binding.updateYears.text.toString().isEmpty())
            binding.updateYears.error="It cannot be empty"

        if(binding.updateOrganisation.text.toString().isEmpty())
            binding.updateOrganisation.error="It cannot be empty"
    }

    private fun checkEmail() {
        if (binding.updateOfficialemaillid.text.toString().isEmpty()) {
            binding.updateOfficialemaillid.error = "Cannot be Empty"


        } else {
            if (binding.updateOfficialemaillid.text.toString().trim { it <= ' ' }
                    .matches(emailPattern.toRegex())) {
               checkPersonalEmail()

            } else {
                binding.updateOfficialemaillid.error = "Invalid Email"

            }
        }
    }



        private fun checkPersonalEmail() {
            if (binding.updatePersonalemaillid.text.toString().isEmpty()
            ) {
                binding.updatePersonalemaillid.error = "Cannot be Empty"
            } else {
                if (binding.updatePersonalemaillid.text.toString().trim { it <= ' ' }
                        .matches(emailPattern.toRegex())) {
                    updateItem()

                } else {
                    binding.updatePersonalemaillid.error = "Invalid Email"

                }
            }
        }





    private fun updateItem() {

            val name=binding.updatePersonName.text.toString()
            val mailid=binding.updateOfficialemaillid.text.toString()
            val pmailid=binding.updatePersonalemaillid.text.toString()
            val designation=binding.updateDesignation.text.toString()
            val phone=binding.updatePhone.text.toString()
            val address=binding.updateAddress.text.toString()
            val total=binding.updateTotal.text.toString()
            val years=binding.updateYears.text.toString()
            val organisation =binding.updateOrganisation.text.toString()
            val birth=binding.updateBirth.text.toString()


        if(inputCheck(name,mailid,pmailid,designation,phone,address,total,years,organisation)){
            //Create User Object

            if(photo!=null){
            val updatedUser= User(args.currentUser.id,photo!!,birth,name,

                mailid,pmailid,
                phone,address,designation,total,
                years,organisation)


            // current user update from user(data class)->suspend function created in repository->
            // in viewmodel created update user

            mUserViewModel.updateUser(updatedUser)

            Toast.makeText(requireContext(),"Updated Successfully",Toast.LENGTH_SHORT).show()
            
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)}

        }else{
            Toast.makeText(requireContext(),"Please fill out all details",Toast.LENGTH_SHORT).show()
        }





    }
    private fun inputCheck(name:String,id:String,mid:String,des:String,
                           phone:String,address:String,total:String,years:String,org:String):Boolean {

        return !(TextUtils.isEmpty(name)||TextUtils.isEmpty(id)||TextUtils.isEmpty(mid)
                || TextUtils.isEmpty(des)|| TextUtils.isEmpty(address)||
                TextUtils.isEmpty(total)|| TextUtils.isEmpty(years)|| TextUtils.isEmpty(org)
                || TextUtils.isEmpty(phone))
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_menu,menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.menu_delete){
            deleteUser()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun deleteUser() {
       val builder=AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes"){_,_->
            mUserViewModel.deleteUser(args.currentUser)
            Toast.makeText(requireContext(),"Successfully Deleted ${args.currentUser.Name}",Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }
        builder.setNegativeButton("No"){_,_-> }
        builder.setTitle("Delete ${args.currentUser.Name}?")
        builder.setMessage("Are you sure you want to delete ${args.currentUser.Name}?")
        builder.create().show()
    }



    private fun hideKeyboard() {
        val imm=requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken,0)
    }

    override fun onDestroyView() {
        view?.setOnTouchListener(null)
        super.onDestroyView()
    }


    private fun locationEnabled():Boolean{
        val locationManager:LocationManager=
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun checkPermission():Boolean{
        if(ActivityCompat.checkSelfPermission
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
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION),PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==PERMISSION_ID){
            if(grantResults.isNotEmpty()&&grantResults[0]==
                    PackageManager.PERMISSION_GRANTED){
                fetchLocation()
            }
        }
    }

    private fun fetchLocation(){
        if(checkPermission()){
            if(locationEnabled()){
                mFusedLocationClient.lastLocation.addOnCompleteListener {task->
                    val location:Location?=task.result
                    if(location!=null){
                        val geocoder=Geocoder(requireContext(), Locale.getDefault())
                        var list:MutableList<Address>?=
                            geocoder.getFromLocation(location.latitude,location.longitude,1)

                        binding.updateAddress.setText(list!![0].getAddressLine(0))
                    }
                }
            }else{
                Toast.makeText(requireContext(),"Please turn on location",Toast.LENGTH_SHORT).show()
            }
        }else{
            requestPermission()
        }
    }
}
