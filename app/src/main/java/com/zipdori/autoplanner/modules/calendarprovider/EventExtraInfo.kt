import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class EventExtraInfo(
    var _id:Int,
    var event_id:Long,
    var photo: Uri?
) : Parcelable