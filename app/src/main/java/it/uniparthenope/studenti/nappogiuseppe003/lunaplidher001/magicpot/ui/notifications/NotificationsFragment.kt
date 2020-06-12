package it.uniparthenope.studenti.nappogiuseppe003.lunaplidher001.magicpot.ui.notifications

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import it.uniparthenope.studenti.nappogiuseppe003.lunaplidher001.magicpot.R
import org.json.JSONObject


class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        val requestQueue = Volley.newRequestQueue(requireContext())
        val titleMail = root.findViewById<EditText>(R.id.box_title)
        val boxMail = root.findViewById<EditText>(R.id.boxMail)

        //Check recipe is wrote correctly
        root.findViewById<Button>(R.id.buttonMail).setOnClickListener(){
            if( titleMail.text.toString() != "" && titleMail.text.toString().length >= 5){
                if( boxMail.text.toString() != "" && boxMail.text.toString().length > 99){
                    sendMail(requestQueue,root)
                } else{
                    Toast.makeText(context, "Inserire la preparazione della ricetta minimo 100 caratteri", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Inserire un titolo", Toast.LENGTH_SHORT).show()
            }
        }
        return root
    }


    //Function to send the recipe wrote
    fun sendMail(requestQueue: RequestQueue, root: View){
        val titleText = root.findViewById<EditText>(R.id.box_title)
        val bodyText = root.findViewById<EditText>(R.id.boxMail)
        val mail = "Titolo Ricetta: " + titleText.text.toString() + "---------Preparazione: " + bodyText.text.toString()
        var myMap  = mutableMapOf<String,String>()
        myMap["message"] = mail
        val jsonObject = JSONObject(myMap as Map<*, *>)
        //Post request passing JSONObject with new recipe
        val getRequest = JsonObjectRequest(
            Request.Method.POST, "http://10.0.2.2:5000/mail", jsonObject,
            Response.Listener { response ->
                Toast.makeText(context, "Inviato con successo", Toast.LENGTH_SHORT).show()
                bodyText.setText("")
                titleText.setText("")
            },
            Response.ErrorListener { error ->
                Toast.makeText(context, "Errore, riprova", Toast.LENGTH_SHORT).show()
            })
        getRequest.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
            // 0 means no retry
            10, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
            1f )// DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        requestQueue.add(getRequest)
    }
}
