package com.developer.aitek.pokemon

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.developer.aitek.api.NetworkConnectionInterceptor
import com.developer.aitek.api.RemoteRequestManager
import com.developer.aitek.api.Repository
import com.developer.aitek.api.data.Moves
import com.developer.aitek.api.data.Types
import com.developer.aitek.pokemon.databinding.ActivityAcitivityDetailBinding

class AcitivityDetail : AppCompatActivity() {

    private lateinit var binding: ActivityAcitivityDetailBinding
    private lateinit var viewModel: ViewModelMain
    private lateinit var factory: ViewModelFactoryMain

    private lateinit var movesAdapter: Adapter<Moves>
    private lateinit var typesAdapter: Adapter<Types>

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = Repository(
            RemoteRequestManager(
                NetworkConnectionInterceptor(this@AcitivityDetail),
                this@AcitivityDetail
            )
        )

        factory = ViewModelFactoryMain(repository)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_acitivity_detail)
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
        movesAdapter = Adapter(R.layout.item_span, mutableListOf(),
            { itemView, item ->
                val itemSpanName = itemView.findViewById<TextView>(R.id.itemSpanName)
                itemSpanName.text = item.move.name
            },
            { _, _ -> })
        binding.detailMoves.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.detailMoves.adapter = movesAdapter

        typesAdapter = Adapter(R.layout.item_span, mutableListOf(),
            { itemView, item ->
                val itemSpanName = itemView.findViewById<TextView>(R.id.itemSpanName)
                itemSpanName.text = item.type.name
            },
            { _, _ -> })
        binding.detailTypes.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.detailTypes.adapter = typesAdapter

        loadDetailData{}
    }

    private fun prepareToLoadLiveCycle() {
        val owner = this
        viewModel.apply {
            isLoading.observe(owner, {
                binding.detailAction1.isEnabled = !it
                binding.detailAction2.isEnabled = !it
                binding.detailLoader.visibility = if (it) View.VISIBLE else View.GONE
            })

            dataDetailRes.observe(owner, { data ->
                if (data.data.name != null) binding.detailName.text = StringBuilder().append(data.data.name)
                    .append("-").append(data.data.febs[data.data.febs.size - 1].fib).toString()
                else binding.detailName.text = data.data.content.name

                Glide.with(this@AcitivityDetail)
                    .load(data.data.content.sprites.back_default)
                    .into(binding.detailCover)

                if (data.meta.is_catch) {
                    binding.detailAction2.visibility = View.VISIBLE
                    binding.detailAction1.text = StringBuilder().append("Bebaskan Pokemon").toString()
                    binding.detailAction2.text = StringBuilder().append("Rename").toString()
                }
                else {
                    binding.detailAction2.visibility = View.GONE
                    binding.detailAction1.text = StringBuilder().append("Tangkap Pokemon").toString()
                }

                binding.detailAction2.setOnClickListener {
                    renameData(tempID, tempDeviceID, { message ->
                        Toast.makeText(this@AcitivityDetail, message, Toast.LENGTH_SHORT)
                            .show()
                    }) { message ->
                        Toast.makeText(this@AcitivityDetail, message, Toast.LENGTH_SHORT)
                            .show()
                        loadDetailData{}
                    }
                }

                binding.detailAction1.setOnClickListener {
                    if (!data.meta.is_catch) catchData(tempID, tempDeviceID, {
                        Toast.makeText(this@AcitivityDetail, it, Toast.LENGTH_SHORT).show()
                    }) {
                        Toast.makeText(this@AcitivityDetail, it, Toast.LENGTH_SHORT).show()
                        showBottomSheetDialog(this@AcitivityDetail, R.layout.sheet_save) { dialog ->
                            val sheetSaveName = dialog.findViewById<EditText>(R.id.sheetSaveName)
                            val sheetSaveAction = dialog.findViewById<Button>(R.id.sheetSaveAction)
                            sheetSaveAction?.text = StringBuilder().append("Simpan").toString()

                            sheetSaveAction?.setOnClickListener {
                                sheetSaveName?.apply {
                                    if (!TextUtils.isEmpty(text.toString())) {
                                        sheetSaveAction.isEnabled = false
                                        saveData(tempID, tempDeviceID, text.toString(), { message ->
                                            sheetSaveAction.isEnabled = true
                                            Toast.makeText(this@AcitivityDetail, message, Toast.LENGTH_SHORT).show()
                                        }) { message ->
                                            Toast.makeText(this@AcitivityDetail, message, Toast.LENGTH_SHORT).show()
                                            loadDetailData {
                                                sheetSaveAction.isEnabled = true
                                                dialog.dismiss()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    else releaseData(tempID, tempDeviceID, {message ->
                        Toast.makeText(this@AcitivityDetail, message, Toast.LENGTH_SHORT).show()
                    }) { message ->
                        Toast.makeText(this@AcitivityDetail, message, Toast.LENGTH_SHORT).show()
                        loadDetailData{}
                    }
                }

                movesAdapter.data = data.data.content.moves
                typesAdapter.data = data.data.content.types
            })
        }
    }

    private fun loadDetailData(onSuccess: () -> Unit) {
        viewModel.detailData(tempID, tempDeviceID, {
            Toast.makeText(this@AcitivityDetail, it, Toast.LENGTH_SHORT).show()
        }) {
            Toast.makeText(this@AcitivityDetail, it, Toast.LENGTH_SHORT)
                .show()
            onSuccess()
        }
    }
}