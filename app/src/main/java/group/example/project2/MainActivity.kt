package group.example.project2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.map ->{
            val intent = Intent(this, MapsActivity::class.java)
//            intent.putExtra("LatLong", getAddress(LatLng(currentLocation.latitude,currentLocation.longitude)))
            startActivity(intent)
            true
        }

        R.id.places ->{
            val intent = Intent(this,PlacesActivity::class.java)
            startActivity(intent)
            true
        }
        R.id.email ->{
            val intent = Intent(this,MailActivity::class.java)
            startActivity(intent)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}