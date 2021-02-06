package com.fct.mvvm.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.fct.mvvm.R
import com.fct.mvvm.data.LaunchEntity
import com.fct.mvvm.data.generateMetaDataString
import com.fct.mvvm.databinding.FragmentLaunchDetailsBinding
import com.fct.mvvm.extensions.returnEmptyStringIfNull
import com.fct.mvvm.viewmodels.LaunchDetailsViewModel
import com.squareup.picasso.Picasso


class LaunchDetailsFragment(
    private val launchEntity: LaunchEntity? = null
) : Fragment() {

    private val detailsViewModel: LaunchDetailsViewModel by viewModels()
    private lateinit var binding: FragmentLaunchDetailsBinding

    //region LifeCycle

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLaunchDetailsBinding.inflate(inflater, container, false)

        // add [LaunchEntity] to the class view model
        launchEntity?.let { detailsViewModel.launchEntity = it }
        setupUIComponents()
        return binding.root
    }

    //endregion

    //region UI

    private fun setupUIComponents() {

        detailsViewModel.launchEntity?.let { entity ->

            // screen title
            (activity as AppCompatActivity).supportActionBar?.title =
                getString(R.string.launch_name, entity.name)

            // patch
            with(binding.imgPatch) {
                Picasso.get()
                    .load(entity.missionPatchSmall)
                    .placeholder(R.mipmap.placeholder)
                    .error(R.mipmap.placeholder)
                    .into(binding.imgPatch)
                contentDescription = entity.name
            }

            // name
            binding.txtName.text = entity.name

            // meta data
            context?.let {
                binding.txtMetaData.text = entity.generateMetaDataString(it)
            }

            // description
            binding.txtDescription.text = entity.details.returnEmptyStringIfNull()

            // external links
            setExternalLinkButton(binding.btnRedditLink, entity.redditUrl)
            setExternalLinkButton(binding.btnArticleLink, entity.articleUrl)
            setExternalLinkButton(binding.btnWebcastLink, entity.webCastUrl)
            setExternalLinkButton(binding.btnWikiLink, entity.wikiUrl)

            // launch photos
            if (entity.flickrImages == null) {
                binding.txtPhotos.visibility = View.GONE
                binding.imgPhotos.visibility = View.GONE
            } else {

                // set initial detail Photo
                detailsViewModel.getNextLaunchPhoto()?.let{
                    setDetailPhoto(it, entity.name)
                }

                // tap to view the next photo
                binding.imgPhotos.setOnClickListener {
                    setDetailPhoto(detailsViewModel.getNextLaunchPhoto(), entity.name)
                }
            }
        }

        // if for some reason the [LaunchEntity] is missing, send the user back to the listing
        if (detailsViewModel.launchEntity == null) activity?.onBackPressed()
    }

    //endregion

    //region Helper Methods

    /**
     * Sets a detail photo
     */
    private fun setDetailPhoto(urlString: String?, name: String) {

        // set photo
        with(binding.imgPatch) {
            Picasso.get()
                .load(urlString)
                .placeholder(R.mipmap.placeholder)
                .error(R.mipmap.placeholder)
                .fit().centerInside()
                .into(binding.imgPhotos)
            contentDescription = name
        }

        // update count text
        binding.txtPhotos.text = getString(
                R.string.details_available_photos,
                detailsViewModel.currentPhotoIndex,
                detailsViewModel.launchEntity?.flickrImages?.size
            )

    }

    /**
     * If this url exist, set the OnClickListener, else hide the button
     */
    private fun setExternalLinkButton(button: Button, urlString: String?) {
        if (urlString.isNullOrBlank()) button.visibility = View.GONE
        else button.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(urlString)
            startActivity(intent)
        }
    }

    //endregion

}