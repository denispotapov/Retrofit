package com.example.retrofit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var jsonPlaceHolderApi: JsonPlaceHolderApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gson = GsonBuilder().serializeNulls().create()

        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                    val originalRequest = chain.request()
                    val newRequest = originalRequest.newBuilder()
                        .header("Interceptor-Header", "xyz")
                        .build()
                    return chain.proceed(newRequest)
                }

            })
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi::class.java)

        getPosts()
        // getComments()
        //createPost()
        //updatePost()
        // deletePost()
    }

    private fun getPosts() {

        val parameters = HashMap<String, String>()
        parameters["userId"] = "1"
        parameters["_sort"] = "id"
        parameters["_order"] = "desc"

        val call = jsonPlaceHolderApi.getPosts(parameters)

        call.enqueue(object : Callback<List<Post>> {
            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                textViewResult.text = t.message
            }

            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (!response.isSuccessful) {
                    textViewResult.text = ("Code" + response.code())
                    return
                }

                val posts = response.body()
                for (post in posts!!) {
                    var content = ""
                    content += "ID :" + post.id + "\n"
                    content += "User ID :" + post.userId + "\n"
                    content += "Title :" + post.title + "\n"
                    content += "Text :" + post.text + "\n\n"

                    textViewResult.append(content)
                }
            }
        })
    }

    private fun getComments() {
        val call = jsonPlaceHolderApi.getComments("posts/3/comments")

        call.enqueue(object : Callback<List<Comment>> {
            override fun onFailure(call: Call<List<Comment>>, t: Throwable) {
                textViewResult.text = t.message
            }

            override fun onResponse(call: Call<List<Comment>>, response: Response<List<Comment>>) {
                if (!response.isSuccessful) {
                    textViewResult.text = ("Code" + response.code())
                    return
                }
                val comments = response.body()
                for (comment in comments!!) {
                    var content = ""
                    content += "ID :" + comment.id + "\n"
                    content += "Post ID :" + comment.postId + "\n"
                    content += "Name :" + comment.name + "\n"
                    content += "Email :" + comment.email + "\n"
                    content += "Text :" + comment.text + "\n\n"

                    textViewResult.append(content)
                }
            }

        })
    }

    private fun createPost() {
        val post = Post(232, "New Title", "New Text")
        val fields = HashMap<String, String>()

        fields["userId"] = "25"
        fields["title"] = "New Title"
        fields["body"] = "New Text"


        val call = jsonPlaceHolderApi.createPost(fields)

        call.enqueue(object : Callback<Post> {

            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    textViewResult.text = ("Code" + response.code())
                    return
                }

                val postResponse = response.body()

                var content = ""
                content += "Code :" + response.code() + "\n"
                content += "ID :" + postResponse?.id + "\n"
                content += "User ID :" + postResponse?.userId + "\n"
                content += "Title :" + postResponse?.title + "\n"
                content += "Text :" + postResponse?.text + "\n"

                textViewResult.append(content)
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                textViewResult.text = t.message
            }
        })
    }

    private fun updatePost() {
        val post = Post(12, null, "New Text")

        val headers = HashMap<String, String>()
        headers["Map-Header1"] = "def"
        headers["Map-Header2"] = "ghi"


        val call = jsonPlaceHolderApi.patchPost(headers, 5, post)

        call.enqueue(object : Callback<Post> {

            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    textViewResult.text = ("Code" + response.code())
                    return
                }

                val postResponse = response.body()

                var content = ""
                content += "Code :" + response.code() + "\n"
                content += "ID :" + postResponse?.id + "\n"
                content += "User ID :" + postResponse?.userId + "\n"
                content += "Title :" + postResponse?.title + "\n"
                content += "Text :" + postResponse?.text + "\n"

                textViewResult.append(content)
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                textViewResult.text = "что то пошло не так"
            }
        })
    }

    private fun deletePost() {
        val call = jsonPlaceHolderApi.deletePost(5)

        call.enqueue(object : Callback<Void> {

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                textViewResult.text = ("Code" + response.code())
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                textViewResult.text = "что то пошло не так"
            }

        })
    }
}



