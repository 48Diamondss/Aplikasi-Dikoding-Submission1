package com.dicoding.aplikasi_dicoding_eventsubmission1.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class UpcomingResponse(
	@field:SerializedName("listEvents")
	val listEvents: List<ListEventsItem> = listOf(),  // List tidak bisa null dan defaultnya adalah list kosong

	@field:SerializedName("error")
	val error: Boolean = false,  // Mengubah menjadi non-null dengan default false

	@field:SerializedName("message")
	val message: String = ""  // Mengubah menjadi non-null dengan default string kosong
)

@Parcelize
data class ListEventsItem(
	@field:SerializedName("imageLogo")
	val imageLogo: String? = null,

	@field:SerializedName("mediaCover")
	val mediaCover: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("ownerName")
	val ownerName: String? = null,

	@field:SerializedName("beginTime")
	val beginTime: String? = null,

	@field:SerializedName("quota")
	val quota: Int? = null,

	@field:SerializedName("registrants")
	val registrants: Int? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("link")
	val link: String? = null
) : Parcelable
