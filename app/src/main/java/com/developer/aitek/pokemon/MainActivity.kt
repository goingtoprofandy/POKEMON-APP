package com.developer.aitek.pokemon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.developer.aitek.api.NetworkConnectionInterceptor
import com.developer.aitek.api.RemoteRequestManager
import com.developer.aitek.api.Repository
import com.developer.aitek.api.data.ItemPokemon
import com.developer.aitek.pokemon.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ViewModelMain
    private lateinit var factory: ViewModelFactoryMain

    private lateinit var adapter: Adapter<ItemPokemon>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = Repository(RemoteRequestManager(
            NetworkConnectionInterceptor(this@MainActivity),
            this@MainActivity
        ))

        factory = ViewModelFactoryMain(repository)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this, factory).get(ViewModelMain::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

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
                tempID = item.id
                startActivity(Intent(this, AcitivityDetail::class.java))
            })

        binding.mainListsPokemon.adapter = adapter
        binding.mainListsPokemon.layoutManager = LinearLayoutManager(this)

        viewModel.loadData {
            Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT)
                .show()
        }
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

            dataRes.observe(owner, {
                adapter.data = it.data
            })
        }
    }
}