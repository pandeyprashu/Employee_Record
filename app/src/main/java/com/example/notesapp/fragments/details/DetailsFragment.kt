package com.example.notesapp.fragments.details

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.notesapp.R
import com.example.notesapp.data.UserViewModel
import com.example.notesapp.databinding.FragmentAddBinding
import com.example.notesapp.databinding.FragmentDetailsBinding
import com.example.notesapp.fragments.list.ListFragmentDirections
import com.example.notesapp.model.User


class DetailsFragment : Fragment(R.layout.fragment_details) {

    private val args by navArgs<DetailsFragmentArgs>()
    private var userList= emptyList<User>()

    private lateinit var mUserViewModel: UserViewModel
    private  var _binding:FragmentDetailsBinding?=null
    private val binding get()=_binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding=FragmentDetailsBinding.bind(view)

        mUserViewModel= ViewModelProvider(this)[UserViewModel::class.java]

        binding.apply {

            if(args.details.image=="drawable://"+R.drawable.ic_person){
                Glide.with(requireContext())
                    .load(R.drawable.ic_person)
                    .into(binding.imageView)


            }else{
            Glide.with(requireContext())
                .load(args.details.image)
                .into(binding.imageView)
            }
            val position=args.details.id
            textName.text = args.details.Name

            textPhone.text = "Phone: "+args.details.Phone
            textOfficialEmail.text = "Official Email Id: "+args.details.OfficialEmail
            textPersonalEmail.text = "Personal Email Id: "+args.details.PersonalEmail
            textAddress.text = "Address: "+args.details.Address
            textDesign.text = "Designation: "+args.details.Designation
            textTotal.text = "Experience: "+args.details.Experience+" Years"
            textYears.text = "Years in Current Organisation: "+args.details.Years
            textOrganisation.text = "Current Organisation: "+args.details.CurrentOrganisation
            textBirth.text=args.details.birth


        }


        setHasOptionsMenu(true)
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
        val builder= AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes"){_,_->
            mUserViewModel.deleteUser(args.details)
            Toast.makeText(requireContext(),"Successfully Deleted ${args.details.Name}", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_detailsFragment_to_listFragment)
        }
        builder.setNegativeButton("No"){_,_-> }
        builder.setTitle("Delete ?")
        builder.setMessage("Are you sure you want to delete ${args.details.Name} details?")
        builder.create().show()
    }

}

