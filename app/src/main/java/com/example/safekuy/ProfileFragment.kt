package com.example.safekuy

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        
        val btnResetData = view.findViewById<android.widget.Button>(R.id.btnResetData)
        btnResetData.setOnClickListener {
            val viewModel = androidx.lifecycle.ViewModelProvider(requireActivity())[com.example.safekuy.viewmodel.TransactionViewModel::class.java]
            android.app.AlertDialog.Builder(requireContext())
                .setTitle("Hapus Semua Data")
                .setMessage("Apakah Anda yakin ingin mereset seluruh transaksi? Data tidak bisa dikembalikan.")
                .setPositiveButton("Hapus") { _, _ ->
                    viewModel.deleteAllTransactions()
                    android.widget.Toast.makeText(requireContext(), "Semua data berhasil dihapus", android.widget.Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Batal", null)
                .show()
        }
        
        return view
    }
}
