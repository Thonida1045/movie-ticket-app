    package com.example.movie_ticket_app.activity

    import android.content.Intent
    import android.os.Bundle
    import android.view.View
    import android.view.ViewGroup
    import android.view.ViewOutlineProvider
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatActivity
    import androidx.recyclerview.widget.LinearLayoutManager
    import com.bumptech.glide.Glide
    import com.bumptech.glide.load.resource.bitmap.CenterCrop
    import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
    import com.bumptech.glide.request.RequestOptions
    import com.example.movie_ticket_app.R
    import com.example.movie_ticket_app.adapter.CastListAdapter
    import com.example.movie_ticket_app.adapter.GenreEachFilmAdapter
    import com.example.movie_ticket_app.databinding.ActivityDetailFilmBinding
    import com.example.movie_ticket_app.model.Film
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.database.DataSnapshot
    import com.google.firebase.database.DatabaseError
    import com.google.firebase.database.FirebaseDatabase
    import com.google.firebase.database.ValueEventListener
    import eightbitlab.com.blurview.RenderScriptBlur


    class DetailFilmActivity : AppCompatActivity() {
        private lateinit var binding: ActivityDetailFilmBinding
        private lateinit var film: Film
        private var isFavorite = false
        
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityDetailFilmBinding.inflate(layoutInflater)
            setContentView(binding.root)

            setVaraible()
        }

        private fun setVaraible() {
            film = intent.getSerializableExtra("object") as Film
            val requestOptions = RequestOptions().transform(
                CenterCrop(),
                GranularRoundedCorners(0f, 0f, 50f, 50f)
            )

            Glide.with(this)
                .load(film.Poster)
                .apply(requestOptions)
                .into(binding.filmPic)
            binding.titleTxt.text = film.Title
            binding.imdbTxt.text = "IMDB ${film.Imdb}"
            binding.moviewTimeTxt.text = "${film.Year} - ${film.Time}"
            binding.movieSummeryTxt.text = film.Description

            binding.backBtn.setOnClickListener {
                finish()
            }

            val radius = 10f
            val decorView = window.decorView
            val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
            val windowBackground = decorView.background

            binding.blurView.setupWith(rootView, RenderScriptBlur(this))
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(radius)
            binding.blurView.outlineProvider = ViewOutlineProvider.BACKGROUND
            binding.blurView.clipToOutline = true

            film.Genre?.let{
                binding.genreView.adapter = GenreEachFilmAdapter(it)
                binding.genreView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

            }

            film.Casts?.let {
                binding.castListView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                binding.castListView.adapter = CastListAdapter(it)
            }
            binding.buyTicketBtn.setOnClickListener {
                val intent= Intent(this, SeatListActivity::class.java)
                intent.putExtra("film", film)
                startActivity(intent)
            }
            
            // Setup favorite button
            checkIfFavorite()
            binding.bookmartBtn.setOnClickListener {
                toggleFavorite()
            }
        }
        
        private fun checkIfFavorite() {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
            val ref = FirebaseDatabase.getInstance().reference
                .child("users")
                .child(uid)
                .child("favorites")
                .child(film.Title ?: return)
            
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    isFavorite = snapshot.exists()
                    updateFavoriteIcon()
                }
                
                override fun onCancelled(error: DatabaseError) {}
            })
        }
        
        private fun updateFavoriteIcon() {
            if (isFavorite) {
                binding.bookmartBtn.setColorFilter(resources.getColor(R.color.yellow, theme))
            } else {
                binding.bookmartBtn.setColorFilter(resources.getColor(R.color.white, theme))
            }
        }
        
        private fun toggleFavorite() {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid == null) {
                Toast.makeText(this, "Please login to add favorites", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                return
            }
            
            val ref = FirebaseDatabase.getInstance().reference
                .child("users")
                .child(uid)
                .child("favorites")
                .child(film.Title ?: return)
            
            if (isFavorite) {
                // Remove from favorites
                ref.removeValue().addOnSuccessListener {
                    Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Add to favorites
                ref.setValue(film).addOnSuccessListener {
                    Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
