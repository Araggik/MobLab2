package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AlertDialog
import androidx.room.Room

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.Serializable
import java.net.URI

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap

    private var markers: MutableMap<LatLng,Uri?> = mutableMapOf()

    private lateinit var dao: PDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).allowMainThreadQueries().build()


        dao = db.pDao()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("markers",markers as Serializable)
        super.onSaveInstanceState(outState)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapClickListener(this)
        mMap.setOnMarkerClickListener(this)

        /*
        for(a in markers)
        {
            mMap.addMarker(MarkerOptions().position(a.key))
        }
        */

        val points = dao.getAllP()

        for(a in points)
        {
            val strPos = a.pos.substringAfter(" ")
            val lat = strPos!!.substringBefore(',').substringAfter("(").toDouble()
            val lng = strPos!!.substringAfter(',').substringBefore(')').toDouble()
            val pos =LatLng(lat, lng)
            mMap.addMarker(MarkerOptions().position(pos))
        }

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun onMapClick(p0: LatLng?) {
        val newMarker = p0!!
        mMap.addMarker(MarkerOptions().position(newMarker))
        //markers[newMarker]=null
        val id = dao.getAllP().size+1
        dao.insertP(Point(id,newMarker.toString(),""))
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        val intent = Intent(this, MarkerActivity::class.java)
        intent.putExtra("pos",p0!!.position.toString())
        //intent.putExtra("path",markers[p0!!.position].toString())
        startActivityForResult(intent,1)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode ==1 && resultCode == Activity.RESULT_OK)
        {
            /*
            val path = Uri.parse(data!!.getStringExtra("path"))
            val strPos = data!!.getStringExtra("pos")!!.substringAfter(" ")
            val lat = strPos!!.substringBefore(',').substringAfter("(").toDouble()
            val lng = strPos!!.substringAfter(',').substringBefore(')').toDouble()
            val pos =LatLng(lat, lng)
            markers[pos]= path

             */
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        //markers = savedInstanceState.getSerializable("markers") as MutableMap<LatLng, Uri?>
        super.onRestoreInstanceState(savedInstanceState)
    }
}