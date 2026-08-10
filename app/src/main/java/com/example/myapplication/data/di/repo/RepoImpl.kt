package com.example.myapplication.data.di.repo

import android.net.Uri
import android.util.Log
import com.example.myapplication.common.ADDTOCARD
import com.example.myapplication.common.ADDTOFAV
import com.example.myapplication.common.PRODUCT_COLLECTION
import com.example.myapplication.common.ResultState
import com.example.myapplication.common.USER_COLLECTION
import com.example.myapplication.domain.di.model.BannerDataModel
import com.example.myapplication.domain.di.model.CartDataModel
import com.example.myapplication.domain.di.model.CategoryDataModel
import com.example.myapplication.domain.di.model.ProductDataModel
import com.example.myapplication.domain.di.model.USerDataParent
import com.example.myapplication.domain.di.model.UserData
import com.example.myapplication.domain.di.model.OrderDataModel
import com.example.myapplication.domain.di.repo.Repo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class RepoImpl @Inject constructor(
    private var firebaseAuth: FirebaseAuth,
    private var firebaseFirestore: FirebaseFirestore,
) : Repo {

    override fun registerUserwithEmailAndPassword(userData: UserData): Flow<ResultState<String>> =
        callbackFlow {

            trySend(ResultState.Loading)

            firebaseAuth.createUserWithEmailAndPassword(
                userData.email,
                userData.password.toString()
            )
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        firebaseFirestore.collection(USER_COLLECTION)
                            .document(it.result.user?.uid.toString())
                            .set(userData).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    trySend(ResultState.Success("User Registered Successfully and add to firestore"))
                                } else {
                                    if (it.exception != null) {
                                        trySend(ResultState.Error(it.exception?.localizedMessage.toString()))
                                    }
                                }
                            }

                        trySend(ResultState.Success("User Registered Successfully"))
                    } else {
                        if (it.exception != null) {
                            trySend(ResultState.Error(it.exception?.localizedMessage.toString()))
                        }
                    }
                }
            awaitClose {
                close()
            }
        }


    override fun loginUserwithEmailAndPassword(userData: UserData): Flow<ResultState<String>> =
        callbackFlow {

            trySend(ResultState.Loading)

            firebaseAuth.signInWithEmailAndPassword(userData.email, userData.password.toString())
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        trySend(ResultState.Success("User Login Successfully"))
                    } else {
                        if (it.exception != null) {
                            trySend(ResultState.Error(it.exception?.localizedMessage.toString()))
                        }
                    }
                }
            awaitClose {
                close()
            }
        }


    override fun getUserById(uid: String): Flow<ResultState<USerDataParent>> = callbackFlow {

        trySend(ResultState.Loading)

        firebaseFirestore.collection(USER_COLLECTION).document(uid).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val document = it.result
                // 1. Convert the document to the UserData object
                val data = document.toObject(UserData::class.java)

                // 2. ONLY proceed if data is NOT null
                if (data != null) {
                    val userDataParent = USerDataParent(document.id, data)
                    trySend(ResultState.Success(userDataParent))
                } else {
                    // If data is null (document doesn't exist), send an error
                    trySend(ResultState.Error("User document is empty or does not exist"))
                }
            } else {
                if (it.exception != null) {
                    trySend(ResultState.Error(it.exception?.localizedMessage.toString()))
                }
            }
        }
        awaitClose {
            close()
        }
    }

    override fun updateUserData(userDataParent: USerDataParent): Flow<ResultState<String>> =
        callbackFlow {

            trySend(ResultState.Loading)

            firebaseFirestore.collection(USER_COLLECTION).document(userDataParent.nodeID)
                .update(userDataParent.userData.toMap())
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        trySend(ResultState.Success("User Data Updated Successfully"))
                    } else {
                        if (it.exception != null) {
                            trySend(ResultState.Error(it.exception?.localizedMessage.toString()))
                        }
                    }
                }
            awaitClose {
                close()
            }
        }

    override fun userProfileImage(uri: Uri): Flow<ResultState<String>> = callbackFlow {

        trySend(ResultState.Loading)

        FirebaseStorage.getInstance().reference.child("userProfileImage/${System.currentTimeMillis()} + ${firebaseAuth.currentUser?.uid}")
            .putFile(uri ?: Uri.EMPTY).addOnCompleteListener {
                it.result.storage.downloadUrl.addOnSuccessListener { imageUri ->

                    trySend(ResultState.Success(imageUri.toString()))

                }
                if (it.exception != null) {
                    trySend(ResultState.Error(it.exception?.localizedMessage.toString()))
                }
            }
        awaitClose {
            close()
        }
    }

    override fun getCategoriesInLimited(): Flow<ResultState<List<CategoryDataModel>>> =
        callbackFlow {

            trySend(ResultState.Loading)

            firebaseFirestore.collection("categories").limit(7).get()
                .addOnSuccessListener { querySnapshot ->
                    val category = querySnapshot.documents.mapNotNull { documment ->

                        documment.toObject(CategoryDataModel::class.java)

                    }
                    trySend(ResultState.Success(category))
                }.addOnFailureListener { exception ->

                    trySend(ResultState.Error(exception.toString()))
                }
            awaitClose {
                close()
            }
        }


    override fun getProductInLimited(): Flow<ResultState<List<ProductDataModel>>> = callbackFlow {
        trySend(ResultState.Loading)

        firebaseFirestore.collection("products").limit(10).get()
            .addOnSuccessListener { querySnapshot ->
                val product = querySnapshot.documents.mapNotNull { document ->

                    document.toObject(ProductDataModel::class.java)?.apply {
                        productId = document.id
                    }

                }
                trySend(ResultState.Success(product))
            }.addOnFailureListener { exception ->

                trySend(ResultState.Error(exception.toString()))
            }
        awaitClose {
            close()
        }
    }

    override fun getAllProducts(): Flow<ResultState<List<ProductDataModel>>> = callbackFlow {
        trySend(ResultState.Loading)

        firebaseFirestore.collection("products").get().addOnSuccessListener {
            val product = it.documents.mapNotNull { document ->
                document.toObject(ProductDataModel::class.java).apply {
                    this?.productId = document.id
                }
            }
            trySend(ResultState.Success(product))
        }.addOnFailureListener {
            trySend(ResultState.Error(it.toString()))
        }
        awaitClose {
            close()
        }
    }

    override fun getProductById(productId: String): Flow<ResultState<ProductDataModel>> =
        callbackFlow {
            if (productId.isBlank()) {
                trySend(ResultState.Error("Invalid Product ID"))
                close()
                return@callbackFlow
            }

            trySend(ResultState.Loading)

            firebaseFirestore.collection("products").document(productId).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        val product = document.toObject(ProductDataModel::class.java)?.apply {
                            this.productId = document.id // Ensure ID is attached
                        }

                        if (product != null) {
                            trySend(ResultState.Success(product))
                        } else {
                            trySend(ResultState.Error("Product not found"))
                        }
                    } else {
                        trySend(ResultState.Error(task.exception?.message ?: "Error"))
                    }
                }
            awaitClose { close() }
        }

    override fun addToCart(cartDataModels: CartDataModel): Flow<ResultState<String>> =
        callbackFlow {
            trySend(ResultState.Loading)

            firebaseFirestore.collection(ADDTOCARD).document(firebaseAuth.currentUser!!.uid)
                .collection("User_Card")
                .add(cartDataModels).addOnSuccessListener {
                    trySend(ResultState.Success("Product Added to Cart"))
                }.addOnFailureListener {
                    trySend(ResultState.Error(it.toString()))
                }
            awaitClose {
                close()
            }
        }

    override fun addToFav(productDataModels: ProductDataModel): Flow<ResultState<String>> =
        callbackFlow {

            trySend(ResultState.Loading)

            firebaseFirestore.collection(ADDTOFAV).document(firebaseAuth.currentUser!!.uid)
                .collection("User_fav").add(productDataModels).addOnSuccessListener {

                    trySend(ResultState.Success("Product Added to Fav"))
                }.addOnFailureListener {
                    trySend(ResultState.Error(it.toString()))
                }
            awaitClose {
                close()
            }
        }


    override fun getallFav(): Flow<ResultState<List<ProductDataModel>>> = callbackFlow {
        trySend(ResultState.Loading)

        firebaseFirestore.collection(ADDTOFAV).document(firebaseAuth.currentUser!!.uid)
            .collection("User_fav")
            .get().addOnSuccessListener {

                val Fav = it.documents.mapNotNull { document ->

                    document.toObject(ProductDataModel::class.java)
                }
                trySend(ResultState.Success(Fav))
            }.addOnFailureListener {
                trySend(ResultState.Error(it.toString()))
            }
        awaitClose {
            close()
        }
    }


    override fun getCart(): Flow<ResultState<List<CartDataModel>>> = callbackFlow {

        val userId = firebaseAuth.currentUser?.uid

        if (userId == null) {
            trySend(ResultState.Error("User not logged in"))
            close()
            return@callbackFlow
        }

        trySend(ResultState.Loading)

        val listener = firebaseFirestore
            .collection(ADDTOCARD)
            .document(userId)
            .collection("User_Card")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    trySend(
                        ResultState.Error(
                            error.message ?: "Failed to load cart"
                        )
                    )
                    return@addSnapshotListener
                }

                if (snapshot != null) {

                    val cartItems = snapshot.documents.mapNotNull { document ->

                        document.toObject(CartDataModel::class.java)?.apply {

                            // VERY IMPORTANT
                            // This is Firestore's random document ID
                            cartId = document.id
                        }
                    }

                    trySend(ResultState.Success(cartItems))
                }
            }

        awaitClose {
            listener.remove()
        }
    }

    override fun getAllCategories(): Flow<ResultState<List<CategoryDataModel>>> = callbackFlow {
        trySend(ResultState.Loading)

        firebaseFirestore.collection("categories").get().addOnSuccessListener {

            val category = it.documents.mapNotNull { document ->

                document.toObject(CategoryDataModel::class.java)
            }
            trySend(ResultState.Success(category))
        }.addOnFailureListener {
            trySend(ResultState.Error(it.toString()))
        }
        awaitClose {
            close()
        }
    }


    override fun getCheckout(productId: String): Flow<ResultState<ProductDataModel>> =
        callbackFlow {
            trySend(ResultState.Loading)

            firebaseFirestore.collection("products").document(productId).get()
                .addOnSuccessListener {

                    val product = it.toObject(ProductDataModel::class.java)?.apply {
                        this.productId = it.id
                    }
                    if (product != null) {
                        trySend(ResultState.Success(product))
                    } else {
                        trySend(ResultState.Error("Product not found"))
                    }

                }.addOnFailureListener {
                    trySend(ResultState.Error(it.toString()))
                }
            awaitClose {
                close()
            }
        }


    override fun getBanner(): Flow<ResultState<List<BannerDataModel>>> = callbackFlow {
        trySend(ResultState.Loading)

        firebaseFirestore.collection("banners").get().addOnSuccessListener {

            val banner = it.documents.mapNotNull { document ->

                document.toObject(BannerDataModel::class.java)
            }
            trySend(ResultState.Success(banner))
        }.addOnFailureListener {
            trySend(ResultState.Error(it.toString()))
        }
        awaitClose {
            close()
        }
    }


    override fun getSpecificCategories(categoryName: String): Flow<ResultState<List<ProductDataModel>>> =
        callbackFlow {

            trySend(ResultState.Loading)

            firebaseFirestore.collection("products").whereEqualTo("category", categoryName).get()
                .addOnSuccessListener {

                    val category = it.documents.mapNotNull { document ->

                        document.toObject(ProductDataModel::class.java)?.apply {
                            this.productId = document.id
                        }
                    }
                    trySend(ResultState.Success(category))
                }.addOnFailureListener {
                    trySend(ResultState.Error(it.toString()))
                }
            awaitClose {
                close()
            }
        }

    override fun getAllSuggestedProducts(): Flow<ResultState<List<ProductDataModel>>> =
        callbackFlow {
            trySend(ResultState.Loading)

            firebaseFirestore.collection(ADDTOFAV).document(firebaseAuth.currentUser!!.uid)
                .collection("User_fav")
                .get().addOnSuccessListener {

                    val fav = it.documents.mapNotNull { document ->

                        document.toObject(ProductDataModel::class.java)
                    }
                    trySend(ResultState.Success(fav))
                }.addOnFailureListener {
                    trySend(ResultState.Error(it.toString()))
                }
            awaitClose {
                close()
            }
        }

    override fun removeFromCart(
        cartId: String
    ): Flow<ResultState<String>> = callbackFlow {

        trySend(ResultState.Loading)

        val userId = firebaseAuth.currentUser?.uid

        if (userId == null) {
            trySend(ResultState.Error("User not logged in"))
            close()
            return@callbackFlow
        }

        if (cartId.isBlank()) {
            trySend(ResultState.Error("Invalid cart ID"))
            close()
            return@callbackFlow
        }

        val document = firebaseFirestore
            .collection(ADDTOCARD)
            .document(userId)
            .collection("User_Card")
            .document(cartId)

        Log.d("CART_DELETE", "Deleting: ${document.path}")

        document.delete()
            .addOnSuccessListener {

                Log.d("CART_DELETE", "Item deleted successfully")

                trySend(
                    ResultState.Success("Item removed successfully")
                )

                close()
            }
            .addOnFailureListener { exception ->

                Log.e(
                    "CART_DELETE",
                    "Delete failed",
                    exception
                )

                trySend(
                    ResultState.Error(
                        exception.message ?: "Failed to remove item"
                    )
                )

                close()
            }

        awaitClose { }
    }

    override fun placeOrder(order: OrderDataModel): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        val docRef = firebaseFirestore.collection("orders").document()
        order.orderId = docRef.id
        docRef.set(order)
            .addOnSuccessListener {
                val userId = firebaseAuth.currentUser?.uid ?: ""
                if (userId.isNotEmpty()) {
                    firebaseFirestore.collection(ADDTOCARD).document(userId)
                        .collection("User_Card").get()
                        .addOnSuccessListener { snapshot ->
                            val batch = firebaseFirestore.batch()
                            for (doc in snapshot.documents) {
                                batch.delete(doc.reference)
                            }
                            batch.commit().addOnCompleteListener {
                                trySend(ResultState.Success(order.orderId))
                                close()
                            }
                        }
                        .addOnFailureListener {
                            trySend(ResultState.Success(order.orderId))
                            close()
                        }
                } else {
                    trySend(ResultState.Success(order.orderId))
                    close()
                }
            }
            .addOnFailureListener {
                trySend(ResultState.Error(it.message ?: "Failed to place order"))
                close()
            }
        awaitClose { }
    }

    override fun getMyOrders(): Flow<ResultState<List<OrderDataModel>>> = callbackFlow {
        trySend(ResultState.Loading)
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            trySend(ResultState.Error("User not logged in"))
            close()
            return@callbackFlow
        }
        
        val listener = firebaseFirestore.collection("orders")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(ResultState.Error(error.message ?: "Failed to load orders"))
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val orders = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(OrderDataModel::class.java)
                    }.sortedByDescending { it.date }
                    trySend(ResultState.Success(orders))
                }
            }
        awaitClose { listener.remove() }
    }
}



