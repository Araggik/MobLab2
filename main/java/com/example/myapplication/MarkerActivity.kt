package com.example.myapplication

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class MarkerActivity : AppCompatActivity(){

    var path:String?=""
    var pos:String?=""
    private lateinit var dao: PDao
    var count:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marker)
        //path = intent.getStringExtra("path")
        pos = intent.getStringExtra("pos")

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).allowMainThreadQueries().build()


        dao = db.pDao()
        val photos  = dao.loadAllByPointPh(pos!!)
        count = dao.getAllPh().size
        val recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
        val adapter = CustomAdapter(photos)
        recyclerView.adapter = adapter
        //dao = (PDao)intent.getSerializableExtra("dao")

        /*if(path!=null && path!="")
        {
            val imageView: ImageView = findViewById(R.id.imageView)
            val uriPhoto = Uri.parse(path)
            imageView.setImageURI(uriPhoto)
        }*/

        /*val b = AlertDialog.Builder(this)
        b.setMessage(id.toString())
        b.create().show()

         */
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getPermissions() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
        }
    }


    private fun createImageFile(): File? {
        val imageFileName = "PNG_" +
                SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()) +
                "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".png", storageDir)

        path = image.absolutePath
        return image
    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun makeOnClick(view: View)
    {
        getPermissions()
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (true/*takePictureIntent.resolveActivity(packageManager) != null*/) {

            val photoFile: File? = createImageFile()

            if (photoFile != null) {
                val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.myapplication.fileprovider",
                        photoFile
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, 1)

            }

        }

    }

    fun saveOnClick(view: View)
    {
        val intent = Intent(this, MapsActivity::class.java)
        intent.putExtra("path", path)
        intent.putExtra("pos", pos)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        /*if(requestCode ==1)
        {
            val imageView: ImageView = findViewById(R.id.imageView)
            val uriPhoto = Uri.parse(path)
            imageView.setImageURI(uriPhoto)
        }


         */
        if(requestCode ==1)
        {
            count = count + 1
            dao.insertPh(Photo(count,pos!!,path!!))
            val uriPhoto = Uri.parse(path)
            val photos  = dao.loadAllByPointPh(pos!!)
            val recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
            val adapter = CustomAdapter(photos)
            recyclerView.adapter = adapter
        }

    }

    override fun onStop() {
     super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
     outState.putString("path",path)
     outState.putString("pos",pos)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        path = savedInstanceState.getString("path")
        pos = savedInstanceState.getString("pos")

/*
if(path!=null && path!="")
{
    val imageView: ImageView = findViewById(R.id.imageView)
    val uriPhoto = Uri.parse(path)
    imageView.setImageURI(uriPhoto)
}
 */

      super.onRestoreInstanceState(savedInstanceState)
    }
}