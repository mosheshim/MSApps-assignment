package mosh.com.assignment

import android.widget.Toast
import androidx.fragment.app.Fragment

class ExtensionsUtils {
//    Extension method to make a toast
    companion object{
        fun Fragment.toast(text:String ,length:Int = Toast.LENGTH_SHORT){
            Toast.makeText(this.requireContext(), text, length).show()
        }
    }
}