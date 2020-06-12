package it.uniparthenope.studenti.nappogiuseppe003.lunaplidher001.magicpot.ui.magicpot

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONArray
import org.json.JSONObject

class PotViewModel : ViewModel() {


    var _text = MutableLiveData<ArrayList<String>>().apply {
        //Cambiare tipo .........^......... per usare altri oggetti
    }
}