package com.example.vetapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.DefaultRetryPolicy
import com.example.vetapp.databinding.FragmentChatGptBinding
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import retrofit2.http.Body
import retrofit2.http.POST

class ChatGptFragment : Fragment() {

    lateinit var responseTV: TextView
    lateinit var questionTV: TextView
    lateinit var queryEdt: TextInputEditText


    private val apiKey =
        "sk-proj-2V8FXtnCAXWY56YnCv8ZT3BlbkFJpXvNaP3IfTY49ReBu1Au" // Replace with your actual API key
    var url = "https://api.openai.com/v1/completions"
    private var _binding: FragmentChatGptBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatGptBinding.inflate(inflater, container, false)
        val view = binding.root

        responseTV = binding.idTVResponse
        questionTV = binding.idTVQuestion
        queryEdt = binding.idEdtQuery

        queryEdt.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                responseTV.text = "Please wait.."
                if (queryEdt.text.toString().isNotBlank()) {
                    //getResponse(queryEdt.text.toString())
                    //getRequestToOpenAI()
                    sendRequestToOpenAI(queryEdt.text.toString())
                } else {
                    Toast.makeText(context, "Please enter your query..", Toast.LENGTH_SHORT).show()
                }
                return@setOnEditorActionListener true
            }
            false
        }
        return view
    }

    /*
    private fun getResponse(query: String) {
        // setting text on for question on below line.
        questionTV.text = query
        queryEdt.setText("")
        // creating a queue for request queue.
        val queue: RequestQueue = Volley.newRequestQueue(context)
        // creating a json object on below line.
        val jsonObject: JSONObject? = JSONObject()
        // adding params to json object.
        jsonObject?.put("model", "text-davinci-003")
        jsonObject?.put("prompt", query)
        jsonObject?.put("temperature", 0)
        jsonObject?.put("max_tokens", 100)
        jsonObject?.put("top_p", 1)
        jsonObject?.put("frequency_penalty", 0.0)
        jsonObject?.put("presence_penalty", 0.0)

        // on below line making json object request.
        val postRequest: JsonObjectRequest =
            // on below line making json object request.
            object : JsonObjectRequest(Method.POST, url, jsonObject,
                Response.Listener { response ->
                    // on below line getting response message and setting it to text view.
                    val responseMsg: String =
                        response.getJSONArray("choices").getJSONObject(0).getString("text")
                    responseTV.text = responseMsg
                },
                // adding on error listener
                Response.ErrorListener { error ->
                    Log.e("TAGAPI", "Error is : " + error.message + "\n" + error)
                }) {
                override fun getHeaders(): kotlin.collections.MutableMap<kotlin.String, kotlin.String> {
                    val params: MutableMap<String, String> = HashMap()
                    // adding headers on below line.
                    params["Content-Type"] = "application/json"
                    params["Authorization"] =
                        "Bearer sk-proj-2V8FXtnCAXWY56YnCv8ZT3BlbkFJpXvNaP3IfTY49ReBu1Au"
                    return params;
                }
            }

        // on below line adding retry policy for our request.
        postRequest.setRetryPolicy(object : RetryPolicy {
            override fun getCurrentTimeout(): Int {
                return 50000
            }

            override fun getCurrentRetryCount(): Int {
                return 50000
            }

            @Throws(VolleyError::class)
            override fun retry(error: VolleyError) {
            }
        })
        // on below line adding our request to queue.
        queue.add(postRequest)
    }
    private fun getRequestToOpenAI() {
        // Define the API endpoint URL
        val url = "https://api.openai.com/v1/chat/completions"

        // Create a JSONObject to hold the request body parameters
        val requestBody = JSONObject().apply {
            put("model", "text-davinci-003")
            put("prompt", "Once upon a time")
            put("max_tokens", 50)
        }

        // Create a request using JsonObjectRequest
        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, requestBody,
            Response.Listener { response ->
                // Handle the response
                val completion = response.getJSONArray("choices").getJSONObject(0).getString("text")
                Log.e("Completion", completion)
                responseTV.text = completion
            },
            Response.ErrorListener { error ->
                // Handle errors
                Log.e("VolleyRequest", "Error: ${error.message}", error)
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Authorization"] = "Bearer sk-proj-2V8FXtnCAXWY56YnCv8ZT3BlbkFJpXvNaP3IfTY49ReBu1Au" // Replace with your API key
                return headers
            }
        }

        // Add the request to the RequestQueue
        Volley.newRequestQueue(context).add(jsonObjectRequest)
    }
    */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun sendRequestToOpenAI(userInput: String) {
        questionTV.text = userInput
        val requestBody = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", userInput)
                })
            })
            put("temperature", 0.5)
            put("top_p", 1)
            put("frequency_penalty", 0)
            put("presence_penalty", 0)
        }

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer $apiKey")
            .post(
                RequestBody.create(
                    "application/json".toMediaTypeOrNull(),
                    requestBody.toString()
                )
            )
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseData = response.body?.string()
                responseData?.let {
                    val jsonResponse = JSONObject(it)
                    val choicesArray = jsonResponse.getJSONArray("choices")
                    if (choicesArray.length() > 0) {
                        val messageContent = choicesArray.getJSONObject(0).getJSONObject("message").getString("content")
                        // Now update your responseTV with the message content
                        activity?.runOnUiThread {
                            responseTV.text = messageContent
                        }
                    }
                }
                //responseTV.text = responseData
                Log.e("VolleyRequest", responseData.toString())
                // Handle the response data here (e.g., update UI with the response)
                // Note: Remember to switch to the main thread if updating UI components
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
