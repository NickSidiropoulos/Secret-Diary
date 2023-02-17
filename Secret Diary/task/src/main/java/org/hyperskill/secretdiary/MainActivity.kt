package org.hyperskill.secretdiary


import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat

const val NAME = "PREF_DIARY"
const val KEY = "KEY_DIARY_TEXT"

class MainActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences

    var noteList = mutableListOf<String>()

    var newText = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val save = findViewById<Button>(R.id.btnSave)
        val undo = findViewById<Button>(R.id.btnUndo)
        val editText = findViewById<EditText>(R.id.etNewWriting)

        sharedPreferences = getSharedPreferences(NAME, Context.MODE_PRIVATE)
        noteList = fillNotesFromSP()
        fillTextView()

        save.setOnClickListener {
            if(editText.text.toString().isNotBlank()){
                val time = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                val parser =  SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val parsedDate = formatter.format(parser.parse(time.toString()))

                noteList.add(parsedDate + "\n" + editText.text)
                editText.text.clear()
                fillTextView()
                newText = true
            } else {
                Toast.makeText(this,"Empty or blank input cannot be saved",Toast.LENGTH_LONG).show()
            }
        }

        undo.setOnClickListener {

                val dialog = AlertDialog.Builder(this)
                    .setTitle("Remove last note")
                    .setMessage("Do you really want to remove the last writing? This operation cannot be undone!")
                    .setPositiveButton("Yes") { _, _ ->
                        if (noteList.size > 0){
                        noteList.removeLast()
                        fillTextView()
                        }
                    }
                    .setNegativeButton("No", null)
                    .create()

                dialog.show()

        }
    }

    private fun fillTextView() {
        val textView = findViewById<TextView>(R.id.tvDiary)
        var temp = ""

        noteList.asReversed().forEachIndexed { index, note ->
            temp += note + if(index == noteList.lastIndex) "" else "\n\n"
        }
        textView.text = temp

        val editor = sharedPreferences.edit()
        editor.putString(KEY, noteList.toString()).apply()

    }

    private fun fillNotesFromSP(): MutableList<String>{

        val savedNotes = sharedPreferences.getString(KEY, "").toString()
        return if (savedNotes.isEmpty()){
            mutableListOf<String>()
        } else {
            savedNotes.split(", ").toMutableList()
        }
    }

}