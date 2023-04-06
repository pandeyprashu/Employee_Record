package com.example.notesapp.fragments.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesapp.R

import com.example.notesapp.data.UserViewModel
import com.example.notesapp.databinding.FragmentListBinding


class ListFragment : Fragment(R.layout.fragment_list) {

    private var _binding:FragmentListBinding?=null
    private val binding get() = _binding!!
    private lateinit var mUserViewModel: UserViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        _binding= FragmentListBinding.bind(view)


        val adapter=ListAdapter()
        binding.apply {
            //Recycler View Connection With Adapter
            recyclerview.layoutManager=LinearLayoutManager(requireContext())
            recyclerview.setHasFixedSize(true)
            recyclerview.itemAnimator=null
            recyclerview.adapter=adapter

            //UserViewModel
            mUserViewModel= ViewModelProvider(this@ListFragment)[UserViewModel::class.java]
            mUserViewModel.readAllData.observe(viewLifecycleOwner, Observer {user->
                adapter.setData(user)

            })
        }


        binding.apply {
            floatingActionButton.setOnClickListener {
                findNavController().navigate(R.id.action_listFragment_to_addFragment)


            }


        }

        setHasOptionsMenu(true)


    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.menu_delete){
            deleteAllUser()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteAllUser() {
        val builder= AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes"){_,_->
            mUserViewModel.deleteAllUsers()
            Toast.makeText(requireContext(),"Successfully Deleted", Toast.LENGTH_SHORT).show()

        }
        builder.setNegativeButton("No"){_,_-> }
        builder.setTitle("Delete ?")
        builder.setMessage("Are you sure you want to delete all data?")
        builder.create().show()
    }




}