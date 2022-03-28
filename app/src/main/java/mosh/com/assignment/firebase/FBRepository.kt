package mosh.com.assignment.firebase

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import mosh.com.assignment.models.Article

class FBRepository {
    private val root = FirebaseDatabase.getInstance().reference.root

    private val _auth = Firebase.auth
    val auth get() = _auth

    private val uid get() = auth.currentUser?.uid

    /**
     * Updates firebase favorites list
     */
    fun saveFavorites(favorites: List<Article>) {
        if (uid != null) {
            root.child(uid!!).setValue(favorites)
        }
    }

    /**
     * Fetch the favorites by the user uid if logged in
     */
    fun fetchFavorites(onFetch: (List<Article>?) -> Unit) {
        if (uid != null) {
            root.child(uid!!).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val articles = mutableListOf<Article>()

                    if (snapshot.exists() && snapshot.hasChildren()) {

                        for (singleSnap in snapshot.children)
                            articles.add(singleSnap.getValue(Article::class.java)!!)
                    }
                    onFetch(articles)
                }
                override fun onCancelled(error: DatabaseError) {
                    onFetch(null)
                }
            })
        }
    }
}