package com.example.driveaware

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.driveaware.databinding.ListItemRecordBinding
import com.example.driveaware.db.RecordTable

class RecordHolder(val binding: ListItemRecordBinding
): RecyclerView.ViewHolder(binding.root) {
    fun bind(record: RecordTable, onRecordClicked: () -> Unit) {
        binding.recordReason.text = record.reason
        binding.recordDate.text = record.date.toString()

        binding.button.setOnClickListener {
            Utils.record = record;
            onRecordClicked()
        }
    }
}

class RecordListAdapter(
    private val records: List<RecordTable>,
    private val onRecordClicked: () -> Unit
) : RecyclerView.Adapter<RecordHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) : RecordHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemRecordBinding.inflate(inflater, parent, false)
        return RecordHolder(binding)
    }

    override fun onBindViewHolder(holder: RecordHolder, position: Int) {
        val record = records[position]
        holder.bind(record, onRecordClicked)
    }

    override fun getItemCount() = records.size
}