package com.developer.aitek.pokemon

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.developer.aitek.api.NetworkConnectionInterceptor
import com.developer.aitek.api.RemoteRequestManager
import com.developer.aitek.api.Repository
import com.developer.aitek.api.data.ItemMyPokemon
import com.developer.aitek.pokemon.databinding.ActivityMyPokemonBinding

class ActivityMyPokemon : AppCompatActivity() {

    private lateinit var binding: ActivityMyPokemonBinding
    private lateinit var viewModel: ViewModelMain
    private lateinit var factory: ViewModelFactoryMain

    private lateinit var adapter: Adapter<ItemMyPokemon>

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_pokemon)

        val repository = Repository(
            RemoteRequestManager(
                NetworkConnectionInterceptor(this@ActivityMyPokemon),
                this@ActivityMyPokemon
            )
        )

        factory = ViewModelFactoryMain(repository)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_pokemon)
        viewModel = ViewModelProvider(this, factory).get(ViewModelMain::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        tempDeviceID = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID)

        // Setup Live Cycle
        prepareToLoadLiveCycle()

        // Setup View
        prepareToView()
    }

    private fun prepareToView() {
        adapter = Adapter(R.layout.item_pokemon, mutableListOf(),
            { itemView, item ->
                val itemPokemonName = itemView.findViewById<TextView>(R.id.itemPokemonName)
                itemPokemonName.text = item.name
            },
            { _, item ->
                tempID = item.pokemon_id
                startActivity(Intent(this, AcitivityDetail::class.java))
            })

        binding.mainListsPokemon.adapter = adapter
        binding.mainListsPokemon.layoutManager = LinearLayoutManager(this)
    }

    private fun prepareToLoadLiveCycle() {
        val owner = this
        viewModel.apply {
            isLoading.observe(owner, {
                if (it) {
                    binding.mainLoader.visibility = View.VISIBLE
                    binding.mainListsPokemon.visibility = View.GONE
                } else {
                    binding.mainLoader.visibility = View.GONE
                    binding.mainListsPokemon.visibility = View.VISIBLE
                }
            })

            dataMyRes.observe(owner, {
                adapter.data = it.data
            })
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.myLists(0, tempDeviceID) {
            Toast.makeText(this@ActivityMyPokemon, it, Toast.LENGTH_SHORT)
                .show()
        }
    }
}