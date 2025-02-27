package com.example.pinterestappclone.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowId
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.pinterestappclone.R
import com.example.pinterestappclone.activity.DetailsActivity
import com.example.pinterestappclone.activity.MainActivity
import com.example.pinterestappclone.activity.MainActivity.Companion.profileMe
import com.example.pinterestappclone.adapter.PhotosAdapter
import com.example.pinterestappclone.adapter.RelatedPhotosAdapter
import com.example.pinterestappclone.model.PhotoItem
import com.example.pinterestappclone.model.PhotoList
import com.example.pinterestappclone.model.RelatedPhotos
import com.example.pinterestappclone.network.RetrofitHttp
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailsFragment(var photoItem: PhotoItem) : Fragment() {

    private lateinit var adapter: RelatedPhotosAdapter
    private lateinit var tvRelated: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = RelatedPhotosAdapter(requireContext() as DetailsActivity)
        apiRelatedPhotos(photoItem.id!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_details_page, container, false)
        initViews(view)
        return view
    }

    @SuppressLint("SetTextI18n")
    private fun initViews(view: View) {
        val ivBtnBack = view.findViewById<ImageView>(R.id.iv_btn_back)
        val ivBtnMore = view.findViewById<ImageView>(R.id.iv_btn_more)
        val ivPhoto = view.findViewById<ImageView>(R.id.iv_photo)
        val ivProfile = view.findViewById<ImageView>(R.id.iv_profile)
        val tvFullName = view.findViewById<TextView>(R.id.tv_fullName)
        val tvFollowers = view.findViewById<TextView>(R.id.tv_followers)
        val tvBtnFollow = view.findViewById<TextView>(R.id.tv_btn_follow)
        val tvTitle = view.findViewById<TextView>(R.id.tv_title)
        val tvDescription = view.findViewById<TextView>(R.id.tv_description)
        val ivBtnComment = view.findViewById<ImageView>(R.id.iv_btn_comment)
        val tvBtnVisit = view.findViewById<TextView>(R.id.tv_btn_visit)
        val tvBtnSave = view.findViewById<TextView>(R.id.tv_btn_save)
        val ivBtnShare = view.findViewById<ImageView>(R.id.iv_btn_share)
        val tvIntro = view.findViewById<TextView>(R.id.tv_intro)
        val ivProfileMe = view.findViewById<ImageView>(R.id.iv_profile_me)
        tvRelated = view.findViewById(R.id.tv_related)

        val rvDetails = view.findViewById<RecyclerView>(R.id.rv_details)
        rvDetails.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rvDetails.adapter = adapter

        val s1 = photoItem.alt_description
        val s2 = photoItem.description
        val s3 = photoItem.user!!.name

        Picasso.get().load(photoItem.urls!!.small)
            .placeholder(ColorDrawable(Color.parseColor(photoItem.color))).into(ivPhoto)

        Picasso.get().load(photoItem.user!!.profile_image!!.large)
            .placeholder(ColorDrawable(Color.parseColor(photoItem.color))).into(ivProfile)

        tvFullName.text = photoItem.user!!.name
        tvFollowers.text = "${photoItem.user!!.total_photos} Followers"
        tvDescription.text = getDescription(s1, s2, s3)

        Picasso.get().load(profileMe)
            .placeholder(ColorDrawable(Color.parseColor(photoItem.color))).into(ivProfileMe)

        ivBtnBack.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun apiRelatedPhotos(id: String) {
        RetrofitHttp.photoService.getRelatedPhotos(id).enqueue(object : Callback<RelatedPhotos> {
            override fun onResponse(call: Call<RelatedPhotos>, response: Response<RelatedPhotos>) {
                val photoList: ArrayList<PhotoItem> = response.body()!!.results!!
                if (photoList.size > 0) {
                    adapter.addPhotos(photoList)
                } else {
                    tvRelated.text = "Related images has not found..."
                }
            }

            override fun onFailure(call: Call<RelatedPhotos>, t: Throwable) {
                Log.e("@@@", t.message.toString())
            }
        })
    }

    private fun getDescription(s1: Any?, s2: String?, s3: String?): String {
        return when {
            s1 != null -> s1.toString()
            s2 != null -> s2.toString()
            else -> "Photo was made by $s3"
        }
    }
}