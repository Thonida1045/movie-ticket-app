package com.example.movie_ticket_app.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.movie_ticket_app.BottomNavHelper
import com.example.movie_ticket_app.adapter.FilmListAdapter
import com.example.movie_ticket_app.adapter.SliderAdapter
import com.example.movie_ticket_app.databinding.ActivityMainBinding
import com.example.movie_ticket_app.model.Film
import com.example.movie_ticket_app.model.SlidersItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private val sliderHandle = Handler() 
    private val sliderRunnable= Runnable {
        binding.viewPager2.currentItem = binding.viewPager2.currentItem + 1

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        initBanner()
        initTopMovies()
        initUpcoming()
        setupBottomNavigation()
        setupUserProfile()
    }

    override fun onResume() {
        super.onResume()
        // Refresh user info when returning to this screen
        updateUserInfo()
    }

    private fun setupUserProfile() {
        // Make profile section clickable
        binding.profileSection.setOnClickListener {
            if (auth.currentUser != null) {
                // Go to profile page if logged in
                startActivity(Intent(this, ProfileActivity::class.java))
            } else {
                // Go to login page if not logged in
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
        updateUserInfo()
    }

    private fun updateUserInfo() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is logged in
            val displayName = currentUser.displayName
            val email = currentUser.email ?: ""
            
            // Show greeting with name
            val greeting = if (!displayName.isNullOrEmpty()) {
                "Hello, $displayName"
            } else {
                // Use email username as fallback
                val username = email.substringBefore("@")
                "Hello, $username"
            }
            binding.tvGreeting.text = greeting
            binding.tvUserEmail.text = email
        } else {
            // User is not logged in
            binding.tvGreeting.text = "Hello, Guest"
            binding.tvUserEmail.text = "Tap to sign in"
        }
    }

    private fun setupBottomNavigation() {
        binding.chipNav.setItemSelected(com.example.movie_ticket_app.R.id.explorer, true)
        with(BottomNavHelper()) {
            this@MainActivity.setupBottomNav(binding.chipNav)
        }
    }

    private fun initTopMovies() {
        val myRef:DatabaseReference=database.getReference("Items")
        binding.progressBarTopMovies.visibility= View.VISIBLE
        val items= ArrayList<Film>()

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    for (i in snapshot.children) {
                        val item=i.getValue(Film::class.java)
                        if(item!=null){
                            items.add(item)
                        }
                    }
                    if(items.isNotEmpty()){
                        binding.recyclerViewTopMovies.layoutManager=
                            LinearLayoutManager(
                                this@MainActivity,
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                        binding.recyclerViewTopMovies.adapter = FilmListAdapter(items)
                    }
                    binding.progressBarTopMovies.visibility= View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Hide progress bar on error
                binding.progressBarTopMovies.visibility = View.GONE
            }


        })
    }

    private fun initBanner(){
        val myRef = database.getReference("Banners")
        binding.progressBarSlider.visibility= View.VISIBLE
        
        myRef.addListenerForSingleValueEvent(object : ValueEventListener  {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists= mutableListOf<SlidersItems>()
                for(i in snapshot.children){
                    val list=i.getValue(SlidersItems::class.java)
                    if (list != null) {
                        lists.add(list)
                    }
                }
                binding.progressBarSlider.visibility= View.GONE
                banners(lists)
            }

            override fun onCancelled(error: DatabaseError) {
                // Hide progress bar on error
                binding.progressBarSlider.visibility = View.GONE
            }

        })
    }
    private fun banners(lists: MutableList<SlidersItems>) {
        binding.viewPager2.adapter= SliderAdapter(lists, binding.viewPager2)
        binding.viewPager2.clipToPadding=false
        binding.viewPager2.clipChildren=false
        binding.viewPager2.offscreenPageLimit=3
        binding.viewPager2.getChildAt(0).overScrollMode=RecyclerView.OVER_SCROLL_NEVER
        
        val compositePageTransformer= CompositePageTransformer().apply{
            addTransformer(MarginPageTransformer(40))
            addTransformer{page, postition ->
                var r=1-Math.abs(postition)
                page.scaleY=0.85f + r * 0.15f
            }
        } 
        
        binding.viewPager2.setPageTransformer(compositePageTransformer)
        binding.viewPager2.currentItem = 1
        binding.viewPager2.registerOnPageChangeCallback( object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandle.removeCallbacks(sliderRunnable)
            }
        })


    }
    private fun initUpcoming() {
        val myRef:DatabaseReference=database.getReference("Upcomming")
        binding.progressBarUpcoming.visibility= View.VISIBLE
        val items= ArrayList<Film>()

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (i in snapshot.children) {
                        val item = i.getValue(Film::class.java)
                        if (item != null) {
                            items.add(item)
                        }
                    }
                    if (items.isNotEmpty()) {
                        binding.recyclerViewUpcoming.layoutManager =
                            LinearLayoutManager(
                                this@MainActivity,
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                        binding.recyclerViewUpcoming.adapter = FilmListAdapter(items)
                    }
                    binding.progressBarUpcoming.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Hide progress bar on error
                binding.progressBarUpcoming.visibility = View.GONE
            }


        })
    }
}