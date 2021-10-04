package com.example.grocerystore.firestore

import android.util.Log
import com.example.grocerystore.activities.*
import com.example.grocerystore.models.*
import com.example.grocerystore.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class Firebase {

    fun uploadUserSignIn(user: User, activity: SignUpActivity){
        FirebaseFirestore.getInstance().collection(Constants.USER_COLLECTION)
            .document(user.id)
            .set(user, SetOptions.merge())
            .addOnSuccessListener {
                activity.uploadUserSuccess()
            }
            .addOnFailureListener {
                activity.hideDialog()
                activity.showErrorSnackBox(it.message)
            }
    }

    fun getUserMainActivity(activity: MainActivity, uid: String){
        FirebaseFirestore.getInstance().collection(Constants.USER_COLLECTION)
                .document(uid)
                .get()
                .addOnSuccessListener {
                    val user: User? = it.toObject(User::class.java)
                    activity.getUserSuccess(user!!)
                }
    }

    fun getUserSplashscreen(activity: SplashscreenActivity, uid: String){
        FirebaseFirestore.getInstance().collection(Constants.USER_COLLECTION)
            .document(uid)
            .get()
            .addOnSuccessListener {
                var user: User? = User()
                user = it.toObject(User::class.java) as User
                activity.getUserSuccess(user)
            }
    }

//    fun getUserProfile(email: String, activity: ProfileActivity){
//        FirebaseFirestore.getInstance().collection(Constants.USER_COLLECTION)
//            .whereEqualTo(Constants.EMAIL, email)
//            .get()
//            .addOnSuccessListener {
//                if (it.documents.size>0){
//                    val users: ArrayList<User> = ArrayList()
//                    for (i in it.documents.indices){
//                        val user: User = it.documents[i].toObject(User::class.java) as User
//                        users.add(user)
//                    }
//                    activity.getUserProfileSuccess(users[0])
//                }else{
//                    activity.hideDialog()
//                    activity.showErrorSnackBox("Cannot load details, please try again")
//                }
//            }
//            .addOnFailureListener {
//                activity.hideDialog()
//                activity.showErrorSnackBox(it.message)
//            }
//    }

    fun getUserProfileActivity(uid: String, activity: ProfileActivity){
        FirebaseFirestore.getInstance().collection(Constants.USER_COLLECTION)
            .document(uid)
            .get()
            .addOnSuccessListener {
                val user: User? = it.toObject(User::class.java)
                activity.getUserProfileSuccess(user!!)
            }
    }

    fun uploadUserProfileActivity(user: User, activity: ProfileActivity){
        FirebaseFirestore.getInstance().collection(Constants.USER_COLLECTION)
                .document(user.id)
                .set(user, SetOptions.merge())
                .addOnSuccessListener {
                    activity.uploadUserSuccess()
                }
                .addOnFailureListener {
                    activity.hideDialog()
                    activity.showErrorSnackBox(it.message)
                }
    }

    fun addToCartMainActivity(map: HashMap<String, ArrayList<Product>>, activity: MainActivity, user: User){
        FirebaseFirestore.getInstance().collection(Constants.CART_COLLECTION)
                .document(user.id)
                .set(map, SetOptions.mergeFields("prodInCart"))
                .addOnSuccessListener {
                    activity.addToCartSuccessful()
                }
                .addOnFailureListener {
                    activity.hideDialog()
                    activity.showErrorSnackBox(it.message)
                }
    }

    fun getCartCartActivity(uid: String, activity: CartActivity){
        FirebaseFirestore.getInstance().collection(Constants.CART_COLLECTION)
                .document(uid)
                .get()
                .addOnSuccessListener {
                    val cart: ArrayList<Product>? =
                        it.toObject(ProductDocument::class.java)?.prodInCart
                    activity.getCartSuccess(cart)
                }
                .addOnFailureListener {
                    activity.hideDialog()
                    activity.showErrorSnackBox(it.message)
                }
    }

    fun uploadOrderCartActivity(order: Order, uid: String, activity: CartActivity){
        FirebaseFirestore.getInstance().collection(Constants.ORDER_COLLECTION)
            .document(uid)
            .set(order, SetOptions.merge())
            .addOnSuccessListener {
                activity.uploadOrderSuccess()
            }
    }

    fun deleteCartItemsOrderSuccess(uid: String, activity: OrderSuccessActivity, map: HashMap<String, ArrayList<Product>>){
        FirebaseFirestore.getInstance().collection(Constants.CART_COLLECTION)
            .document(uid)
            .set(map)
            .addOnSuccessListener {
                activity.deleteSuccess()
            }
    }

    fun uploadCartCartActivity(uid: String, map: HashMap<String, ArrayList<Product>>, activity: CartActivity){
        FirebaseFirestore.getInstance().collection(Constants.CART_COLLECTION)
            .document(uid)
            .set(map, SetOptions.merge())
            .addOnSuccessListener {
                activity.uploadCartSuccess()
            }
    }

    fun addToCartSearchActivity(uid: String, map: HashMap<String, ArrayList<Product>>, activity: SearchActivity){
        FirebaseFirestore.getInstance().collection(Constants.CART_COLLECTION)
            .document(uid)
            .set(map, SetOptions.mergeFields("prodInCart"))
            .addOnSuccessListener {
                activity.addToCartSuccess()
             }
    }

    fun addToCartSearchActivityResults(uid: String, map: HashMap<String, ArrayList<Product>>, activity: SearchResultsActivity){
        FirebaseFirestore.getInstance().collection(Constants.CART_COLLECTION)
            .document(uid)
            .set(map, SetOptions.mergeFields("prodInCart"))
            .addOnSuccessListener {
                activity.uploadCartSuccess()
            }
    }

    fun getCartPaymentActivity(uid: String, activity: PaymentActivity){
        FirebaseFirestore.getInstance().collection(Constants.CART_COLLECTION)
            .document(uid)
            .get()
            .addOnSuccessListener {
                val products: ProductDocument = it.toObject(ProductDocument::class.java) as ProductDocument
                activity.getCartSuccess(products.prodInCart)
            }
            .addOnFailureListener {
                activity.hideDialog()
                activity.showErrorSnackBox(it.message)
            }
    }

    fun getOrdersPaymentActivity(uid: String, activity: PaymentActivity){
        FirebaseFirestore.getInstance().collection(Constants.ORDER_COLLECTION)
            .document(uid)
            .get()
            .addOnSuccessListener {
                val orderDocument: OrderDocument = it.toObject(OrderDocument::class.java) as OrderDocument
                activity.getOrderSuccess(orderDocument)
            }
    }

    fun addToCartCategorySearchResults(uid: String, map: HashMap<String, ArrayList<Product>>, activity: CategorySearchResultsActivity){
        FirebaseFirestore.getInstance().collection(Constants.CART_COLLECTION)
            .document(uid)
            .set(map, SetOptions.mergeFields("prodInCart"))
            .addOnSuccessListener {
                activity.addToCartSuccess()
            }
    }

    fun deleteOrderOrderSuccess(uid: String, order: Order, activity: OrderSuccessActivity){
        FirebaseFirestore.getInstance().collection(Constants.ORDER_COLLECTION)
            .document(uid)
            .set(order)
            .addOnSuccessListener {
                activity.deleteOrderSuccess()
            }
    }
}