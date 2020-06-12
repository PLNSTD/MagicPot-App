package it.uniparthenope.studenti.nappogiuseppe003.lunaplidher001.magicpot.ui.fullrecipe


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import it.uniparthenope.studenti.nappogiuseppe003.lunaplidher001.magicpot.R
import it.uniparthenope.studenti.nappogiuseppe003.lunaplidher001.magicpot.ui.resultmagicpot.RecModel

class FullRecipe : Fragment() {

    private lateinit var recipeViewModel: RecipeViewModel


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        recipeViewModel =
                activity?.let { ViewModelProviders.of(it).get(RecipeViewModel::class.java) }!!
        val root = inflater.inflate(R.layout.fragment_recipe, container, false)

        var fullRicetta : RecModel

        val recTitle = root.findViewById<TextView>(R.id.fullRecT)
        val recDes = root.findViewById<TextView>(R.id.des_recipe)
        var recImage = root.findViewById<ImageView>(R.id.foto_recipe)
        var recIngredients = root.findViewById<TextView>(R.id.ingr_recipe)


        recipeViewModel._text.observe(viewLifecycleOwner, Observer {
            fullRicetta = recipeViewModel._text.value!!

            //Showing all recipe details
            recTitle.text = fullRicetta.title
            recDes.text = fullRicetta.des
            recImage.setImageBitmap(fullRicetta.image)

            //Text correction
            recDes.text = recDes.text.toString().replace("xa0","")
            recDes.text = recDes.text.toString().replace("\\","")
            recDes.text = recDes.text.toString().replace("['","")
            recDes.text = recDes.text.toString().replace("[\"","")
            recDes.text = recDes.text.toString().replace("']","")
            recDes.text = recDes.text.toString().replace("\"","")
            recDes.text = recDes.text.toString().replace(".,",".")
            recDes.text = recDes.text.toString().replace(",,",",")

            //Showing recipe ingredients
            val iList = fullRicetta.ingredients
            for(i in 0 until iList.size){
                recIngredients.text = recIngredients.text.toString() + "• " + iList[i]
            }

            //Make trasparent recipe difficulty stars
            if(fullRicetta.diff == "Facile" || fullRicetta.diff == "Molto facile"){
                root.findViewById<ImageView>(R.id.diffImage2).alpha = 0.5F
                root.findViewById<ImageView>(R.id.diffImage3).alpha = 0.5F
            }
            else if(fullRicetta.diff == "Media"){
                root.findViewById<ImageView>(R.id.diffImage3).alpha = 0.5F
            }
        })


        return root
    }
}