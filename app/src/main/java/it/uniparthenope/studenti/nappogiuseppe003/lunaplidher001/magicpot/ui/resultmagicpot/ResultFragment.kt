package it.uniparthenope.studenti.nappogiuseppe003.lunaplidher001.magicpot.ui.resultmagicpot


import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

import it.uniparthenope.studenti.nappogiuseppe003.lunaplidher001.magicpot.R
import it.uniparthenope.studenti.nappogiuseppe003.lunaplidher001.magicpot.ui.fullrecipe.RecipeViewModel
import it.uniparthenope.studenti.nappogiuseppe003.lunaplidher001.magicpot.ui.magicpot.PotViewModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.RuntimeException
import kotlin.collections.ArrayList

class ResultFragment : Fragment(){

    private lateinit var potViewModel: PotViewModel
    private lateinit var recipeViewModel: RecipeViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        //ViewModels for data sharing
        potViewModel = activity?.let { ViewModelProviders.of(it).get(PotViewModel::class.java) }!!
        recipeViewModel = activity?.let { ViewModelProviders.of(it).get(RecipeViewModel::class.java) }!!
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.result_fragment, container, false)

        val requestQueue = Volley.newRequestQueue(context)

        //Positioning RecyclerView CardViews in a GridLayout
        potViewModel._text.observe(viewLifecycleOwner, Observer {
            val myArray = potViewModel._text.value!!
            getRecipes(requestQueue,root,myArray)
        })
        return root
    }

    fun getRecipes(requestQueue: RequestQueue, root: View, myArray: ArrayList<String>){
        var myMap  = mutableMapOf<String,ArrayList<String>>()
        myMap["Ingredients"] = myArray
        val jsonObject = JSONObject(myMap as Map<*, *>)
        var jArray = JSONArray()
        jArray.put(jsonObject)
        var arrayList = ArrayList<RecModel>()

        //Post request to send ingredients and receive recipes
        val postRequest = JsonArrayRequest(
            Request.Method.POST,"http://10.0.2.2:5000/lsa", jArray,
            Response.Listener { response ->
                try {
                    val rV = root.findViewById<RecyclerView>(R.id.recViewRecipe)
                    val myAdapter = RecAdapter(arrayList)
                    rV.adapter = myAdapter
                    rV.layoutManager = GridLayoutManager(context, 2)
                    CardViewCreator(response, arrayList)

                    //Selected recipe
                    myAdapter.mOnItem = object : RecAdapter.OnClick {
                        override fun onItemClick(index: Int) {
                            recipeViewModel._text.value = arrayList[index]
                            findNavController().navigate(R.id.action_result_to_recipe)
                        }
                    }
                }catch (e : JSONException){
                    throw RuntimeException(e)
                }
            },
            Response.ErrorListener { error ->
                //Toast.makeText(context,"qualcosa e' andato storto", Toast.LENGTH_SHORT).show()
            })
        postRequest.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
            // 0 means no retry
            10, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
            1f )// DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        requestQueue.add(postRequest)
    }

    //Function to create recipes CardViews
    fun CardViewCreator(arrayJson : JSONArray, arrayList : ArrayList<RecModel>) {
        for(i in 0 until arrayJson.length()) {
            val jObject = arrayJson.getJSONObject(i)

            //Image string decoder
            val modString = jObject.getString("image").removeRange(0,2)
            modString.removeRange(modString.length-1,modString.length)
            val imageBytes = android.util.Base64.decode(modString, android.util.Base64.DEFAULT);
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size);

            //Manage recipe ingredients
            val ingJson : JSONArray = jObject.getJSONArray("Ingredients")
            var myString : String
            var iList = ArrayList<String>()
            for(j in 0 until ingJson.length()){
                val oBject = ingJson.getJSONObject(j)
                myString = oBject.getString("name_i") + " " + oBject.getString("quantity") + "\n"
                iList.add(myString)
            }

            //Add to RecyclerView the complete recipe
            arrayList.add(RecModel(jObject.optString("name_recipe"),decodedImage,jObject.optString("decr"),jObject.optString("difficulty"),iList))
        }
    }

}
