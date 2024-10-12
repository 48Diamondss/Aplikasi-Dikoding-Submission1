package com.dicoding.aplikasi_dicoding_eventsubmission1.data.response

import com.google.gson.annotations.SerializedName

data class UpcomingResponse(
	@field:SerializedName("listEvents")
	val listEvents: List<ListEventsItem> = listOf(),  // List tidak bisa null dan defaultnya adalah list kosong

	@field:SerializedName("error")
	val error: Boolean = false,  // Mengubah menjadi non-null dengan default false

	@field:SerializedName("message")
	val message: String = ""  // Mengubah menjadi non-null dengan default string kosong
)

data class ListEventsItem(
	@field:SerializedName("imageLogo")
	val imageLogo: String? = null,  // Tetap nullable jika bisa tidak ada

	@field:SerializedName("mediaCover")
	val mediaCover: String? = null,  // Tetap nullable jika bisa tidak ada

	@field:SerializedName("name")
	val name: String? = null,  // Tetap nullable jika bisa tidak ada

	@field:SerializedName("ownerName")
	val ownerName: String? = null,  // Tetap nullable jika bisa tidak ada

	@field:SerializedName("beginTime")
	val beginTime: String? = null,  // Tetap nullable jika bisa tidak ada

	@field:SerializedName("quota")
	val quota: Int? = null,  // Tetap nullable jika bisa tidak ada

	@field:SerializedName("registrants")
	val registrants: Int? = null,  // Tetap nullable jika bisa tidak ada

	@field:SerializedName("description")
	val description: String? = null,  // Tetap nullable jika bisa tidak ada

	@field:SerializedName("link")
	val link: String? = null  // Tetap nullable jika bisa tidak ada
)
