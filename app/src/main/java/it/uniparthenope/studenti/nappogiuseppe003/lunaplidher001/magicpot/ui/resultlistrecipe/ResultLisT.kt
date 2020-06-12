package it.uniparthenope.studenti.nappogiuseppe003.lunaplidher001.magicpot.ui.resultmagicpot


import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

import it.uniparthenope.studenti.nappogiuseppe003.lunaplidher001.magicpot.R
import it.uniparthenope.studenti.nappogiuseppe003.lunaplidher001.magicpot.ui.fullrecipe.RecipeViewModel
import it.uniparthenope.studenti.nappogiuseppe003.lunaplidher001.magicpot.ui.magicpot.PotViewModel
import it.uniparthenope.studenti.nappogiuseppe003.lunaplidher001.magicpot.ui.resultlistrecipe.ResultListViewModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.RuntimeException
import kotlin.collections.ArrayList

class ResultLisT : Fragment(){

    private lateinit var resultViewModel: ResultListViewModel
    private lateinit var recipeViewModel: RecipeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //View models for data sharing
        resultViewModel = activity?.let { ViewModelProviders.of(it).get(ResultListViewModel::class.java) }!!
        recipeViewModel = activity?.let { ViewModelProviders.of(it).get(RecipeViewModel::class.java) }!!
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.result_fragment, container, false)

        //Positioning RecyclerView CardViews in a GridLayout
        resultViewModel._text.observe(viewLifecycleOwner, Observer {
            val myRecipe = resultViewModel._text.value!!
            val arrayList = ArrayList<RecModel>()
            val rV = root.findViewById<RecyclerView>(R.id.recViewRecipe)
            val myAdapter = RecAdapter(arrayList)
            rV.adapter = myAdapter
            rV.layoutManager = GridLayoutManager(context, 2)
            PrintRecipe(myRecipe,arrayList)
            //Recipe selected
            myAdapter.mOnItem = object : RecAdapter.OnClick {
                override fun onItemClick(index: Int) {
                    recipeViewModel._text.value = arrayList[index]
                    findNavController().navigate(R.id.action_resultlist_to_recipe)
                }
            }
        })
        return root
    }

    //Function to create recipes CardViews
    fun PrintRecipe(arrayJson: JSONArray, arrayList: ArrayList<RecModel>){
        val jObject = arrayJson.getJSONObject(0)

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
