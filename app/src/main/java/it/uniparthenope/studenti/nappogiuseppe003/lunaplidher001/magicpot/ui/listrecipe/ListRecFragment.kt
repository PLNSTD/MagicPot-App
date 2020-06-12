package it.uniparthenope.studenti.nappogiuseppe003.lunaplidher001.magicpot.ui.listrecipe

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import it.uniparthenope.studenti.nappogiuseppe003.lunaplidher001.magicpot.R
import it.uniparthenope.studenti.nappogiuseppe003.lunaplidher001.magicpot.ui.fullrecipe.RecipeViewModel
import it.uniparthenope.studenti.nappogiuseppe003.lunaplidher001.magicpot.ui.resultlistrecipe.ResultListViewModel
import it.uniparthenope.studenti.nappogiuseppe003.lunaplidher001.magicpot.ui.resultmagicpot.RecAdapter
import it.uniparthenope.studenti.nappogiuseppe003.lunaplidher001.magicpot.ui.resultmagicpot.RecModel
import org.json.JSONArray

class ListRecFragment : Fragment() {

    private lateinit var listRecViewModel: ListRecViewModel
    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var resultViewModel: ResultListViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        listRecViewModel =
            ViewModelProviders.of(this).get(ListRecViewModel::class.java)
        recipeViewModel =
            activity?.let { ViewModelProviders.of(it).get(RecipeViewModel::class.java) }!!
        val root = inflater.inflate(R.layout.fragment_allrec, container, false)
        val requestQueue = Volley.newRequestQueue(requireContext())

        //Buttons for page control
        var page = 1
        val btn_prev = root.findViewById<Button>(R.id.btn_prev)
        val pageText = root.findViewById<TextView>(R.id.pag_corr)
        val btn_next = root.findViewById<Button>(R.id.btn_next)

        btn_prev.isEnabled = false
        pageText.text = "Pagina " + page.toString()
        getTitles(requestQueue,root, requireContext() as FragmentActivity)

        PrintPage(page,root,requestQueue)

        //Go Previous Page
        btn_prev.setOnClickListener() {
            if (page > 1) {
                page--
                PrintPage(page, root, requestQueue)
                pageText.text = "Pagina " + page.toString()
            }
            else if (page == 1) btn_prev.isEnabled = false
            if (page < 482 ) btn_next.isEnabled = true
        }
        //Go Next Page
        btn_next.setOnClickListener(){
            page++
            PrintPage(page,root,requestQueue)
            pageText.text = "Pagina " + page.toString()
            if(page > 1) btn_prev.isEnabled = true
            if(page == 482) btn_next.isEnabled = false
        }
        return root
    }


    //Function for request all recipes
    fun getTitles(requestQueue: RequestQueue, root: View, conText : FragmentActivity){
        var recipes = ArrayList<String>()
        resultViewModel = activity?.let { ViewModelProviders.of(it).get(ResultListViewModel::class.java) }!!
        val autoCompleteTextView: AutoCompleteTextView = root.findViewById(R.id.window_rec)

        //Request all recipes titles
        val getRequest = JsonArrayRequest(
            Request.Method.GET, "http://10.0.2.2:5000/allRecipe", null,
            Response.Listener { response ->
                recipesAuto(response,recipes)
                val adapter = ArrayAdapter<String>(conText,android.R.layout.simple_list_item_1,recipes)
                autoCompleteTextView.setAdapter(adapter)
                autoCompleteTextView.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {

                        //Request one recipe by name_recipe in autoCompleteTextView
                        val recipeRequest = JsonArrayRequest(
                            Request.Method.GET, "http://10.0.2.2:5000/allRecipe/${autoCompleteTextView.text}", null,
                            Response.Listener { response ->
                                resultViewModel._text.value = response
                                findNavController().navigate(R.id.action_list_to_result)
                        },
                        Response.ErrorListener { error ->
                            //Toast.makeText(context, "Riprova", Toast.LENGTH_SHORT).show()
                        })
                        recipeRequest.retryPolicy = DefaultRetryPolicy(
                            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                            // 0 means no retry
                            10, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
                            1f )// DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                        requestQueue.add(recipeRequest)
                        autoCompleteTextView.setText("")
                        return@OnKeyListener true
                    }
                    false
                })
            },
            Response.ErrorListener { error ->
                //Toast.makeText(activity, "qualcosa e' andato storto", Toast.LENGTH_SHORT).show()
            })
        getRequest.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
            // 0 means no retry
            10, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
            1f )// DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        requestQueue.add(getRequest)
    }


    //Function to manage titles response
    fun recipesAuto(arrayJson : JSONArray, arrayList : ArrayList<String>) {
        for(i in 0 until arrayJson.length()) {
            val jObject = arrayJson.getJSONObject(i)
            arrayList.add(jObject.optString("name_recipe"))
        }
    }


    //Function to manage recipes
    fun PrintPage(page : Int, root : View, requestQueue : RequestQueue){
        var arrayList = ArrayList<RecModel>()
        var Alist = ArrayList<String>()
        val myAdapter = RecAdapter(arrayList)
        //Request to manage recipes by page
        val getRequest = JsonArrayRequest(
            Request.Method.GET, "http://10.0.2.2:5000/allRecipe/$page", null,
            Response.Listener { response ->
                val rV = root.findViewById<RecyclerView>(R.id.recViewRecipe)
                rV.layoutManager = GridLayoutManager(activity, 2)
                rV.adapter = myAdapter
                CardViewCreator(response, arrayList)

                //Show fragment of selected recipe
                myAdapter.mOnItem = object : RecAdapter.OnClick {
                    override fun onItemClick(index: Int) {
                        recipeViewModel._text.value = arrayList[index]
                        findNavController().navigate(R.id.action_all_to_recipe)
                    }
                }
            },
            Response.ErrorListener { error ->
                //Toast.makeText(context, "qualcosa e' andato storto", Toast.LENGTH_SHORT).show()
            })
        getRequest.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
            // 0 means no retry
            10, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
            1f )// DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        requestQueue.add(getRequest)
    }


    //Function to create recipes CardViews
    fun CardViewCreator(arrayJson : JSONArray, arrayList : ArrayList<RecModel>) {
        for(i in 0 until arrayJson.length()) {
            val jObject = arrayJson.getJSONObject(i)

            //Image string decoder
            val modString = jObject.getString("image").removeRange(0,2)
            modString.removeRange(modString.length-1,modString.length)
            val imageBytes = android.util.Base64.decode(modString, android.util.Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

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
