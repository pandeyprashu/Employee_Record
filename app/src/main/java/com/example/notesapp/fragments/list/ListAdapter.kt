package com.example.notesapp.fragments.list

import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.notesapp.R
import com.example.notesapp.model.User
import com.example.notesapp.databinding.ItemLayoutBinding
import java.io.ByteArrayOutputStream

class ListAdapter:RecyclerView.Adapter<ListAdapter.MyViewHolder>() {
    private var userList= emptyList<User>()

    class MyViewHolder(val binding: ItemLayoutBinding):RecyclerView.ViewHolder(binding.root){



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding=ItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        with(holder){
            with(userList[position]){


                if(image=="drawable://"+R.drawable.ic_person){
                    Glide.with(itemView)
                        .load(R.drawable.ic_person)
                        .into(binding.employeeImageView)


                }else{

                Glide.with(itemView)
                    .load(image)
                    .into(binding.employeeImageView)
                }

                binding.textDate.text=this.birth
                binding.textName.text=this.Name
                binding.textDesign.text="Designation: "+this.Designation
                binding.textPhone.text="Phone: "+this.Phone
                binding.textEmail.text="Official Email Id: "+this.OfficialEmail
                binding.textYears.text="Working Years in Current Organisation: "+this.Years

                binding.itemLayout.setOnClickListener {
                    val action=ListFragmentDirections.actionListFragmentToDetailsFragment(userList[position]) // to do task pass argument
                    //task is to give edit button in cardview for the changes and perform the edit action there
                    itemView.findNavController().navigate(action)
                }
                binding.updateBtn.setOnClickListener {
                    val action=ListFragmentDirections.actionListFragmentToUpdateFragment(userList[position])
                    itemView.findNavController().navigate(action)
                }




            }


        }
    }
    fun setData(user:List<User>){
        this.userList=user
        notifyDataSetChanged()

    }
}