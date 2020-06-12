package it.uniparthenope.studenti.nappogiuseppe003.lunaplidher001.magicpot.ui.magicpot

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import it.uniparthenope.studenti.nappogiuseppe003.lunaplidher001.magicpot.R
import org.json.JSONObject


class PotFragment : Fragment(){

    private lateinit var potViewModel: PotViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val requestQueue = Volley.newRequestQueue(context)
        getIngredients(requestQueue,root)
        return root
    }

    //Request for DB Ingredients
    fun getIngredients(requestQueue : RequestQueue, root : View){
        potViewModel =
            activity?.let { ViewModelProviders.of(it).get(PotViewModel::class.java) }!!
        val getRequest = JsonObjectRequest(
            Request.Method.GET, "http://10.0.2.2:5000/allIngredients", null,
            Response.Listener { response ->
                ingredientSelection(response,root)
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

    //Manage ingredients selected by user
    fun ingredientSelection(response : JSONObject,root: View){

        //RecyclerView of Ingredients CardView
        val arrayList = ArrayList<Model>()
        var ingredients = ArrayList<String>()
        val myAdapter = MyAdapter(arrayList)
        val rV = root.findViewById<RecyclerView>(R.id.recyclerView)
        rV.layoutManager = LinearLayoutManager(activity)
        rV.adapter = myAdapter

        //SearchBar ingredients creation
        ingredientsAuto(response,ingredients)
        val adapter = activity?.let { ArrayAdapter<String>(it,android.R.layout.simple_list_item_1,ingredients) }
        val autoCompleteTextView: AutoCompleteTextView = root.findViewById(R.id.window_ingredients)
        autoCompleteTextView.setAdapter(adapter)

        //Buttons
        val btnRecipes = root.findViewById<Button>(R.id.send_btn)
        val btnInsert = root.findViewById<ImageButton>(R.id.imageButton3)

        //CopyList of chosen ingredients
        val arrayDin = ArrayList<String>()

        //softInput for KeyBoard Hiding
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        //Insert by key
        autoCompleteTextView.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                val result = ingredients.contains(autoCompleteTextView.text.toString())
                val resultCopy = arrayDin.contains(autoCompleteTextView.text.toString())

                //Check correct ingredient and not already copied
                if(result and !resultCopy) {
                    imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)
                    val myString = autoCompleteTextView.text.toString()
                    autoCompleteTextView.setText("")
                    arrayDin.add(myString)
                    arrayList.add(Model(myString))
                }
                else{ //Error Alert
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Ingrediente errato")
                    builder.setMessage("Selezionare un ingrediente dalla lista")
                    builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                        Toast.makeText(context,
                            android.R.string.yes, Toast.LENGTH_SHORT)
                    }
                    autoCompleteTextView.setText("")
                    builder.show()
                }
                return@OnKeyListener true
            }
            false
        })

        //Insert by button
        btnInsert.setOnClickListener{
            val result = ingredients.contains(autoCompleteTextView.text.toString())
            val resultCopy = arrayDin.contains(autoCompleteTextView.text.toString())

            //Check correct ingredient and not already copied
            if(result and !resultCopy) {
                imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)
                val myString = autoCompleteTextView.text.toString()
                autoCompleteTextView.setText("")
                arrayDin.add(myString)
                arrayList.add(Model(myString))
            }
            else{
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Ingrediente errato o duplicato")
                builder.setMessage("Selezionare un ingrediente dalla lista")
                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                    Toast.makeText(context,
                        android.R.string.yes, Toast.LENGTH_SHORT)
                }
                builder.show()
                autoCompleteTextView.setText("")
            }
            imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)
        }

        //Ingredient removal
        myAdapter.mOnItem = object : MyAdapter.OnClick{
            override fun onItemClick(index: Int) {
                arrayList.removeAt(index)
                myAdapter.notifyItemRemoved(index)
                myAdapter.notifyItemRangeChanged(index, arrayList.size)
                arrayDin.removeAt(index)
            }
        }

        //Search best recipes by ingredients selected
        btnRecipes.setOnClickListener(){
            if(arrayList.size > 2){
                imm?.hideSoftInputFromWindow(view?.getWindowToken(), 0)

                //Correction to lowercase/without whitespaces for Server
                var myArray = ArrayList<String>()
                for(element in arrayDin){
                    myArray.add(element.toLowerCase().replace("\\s".toRegex(),""))
                }
                potViewModel._text.value = myArray
                this.findNavController().navigate(R.id.action_pot_to_result)
            }
            else{
                //Insert error (min 3 ingredients
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Attenzione")
                builder.setMessage("Inserire almeno 3 ingredienti")
                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                    Toast.makeText(context,
                        android.R.string.yes, Toast.LENGTH_SHORT)
                }
                builder.show()
            }
        }
    }


    //Creating list for autoCompleteTextView
    fun ingredientsAuto(arrayJson: JSONObject, arrayList: ArrayList<String>) {
        val jObject = arrayJson.getJSONArray("dict")
        for(i in 0 until jObject.length()) {
            val jarray = jObject.getJSONObject(i)
            arrayList.add(jarray.optString("name"))
        }
    }
}









