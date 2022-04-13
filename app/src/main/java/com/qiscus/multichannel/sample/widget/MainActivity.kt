package com.qiscus.multichannel.sample.widget

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.qiscus.multichannel.QiscusMultichannelWidgetConfig
import com.qiscus.multichannel.sample.R
import com.qiscus.multichannel.sample.widget.service.FirebaseServices
import com.qiscus.multichannel.util.MultichannelConst
import com.qiscus.multichannel.util.QiscusChatRoomBuilder
import com.qiscus.multichannel.util.getAuthority
import com.qiscus.sdk.chat.core.data.model.QChatRoom
import kotlinx.android.synthetic.main.activity_main.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private val qiscusMultichannelWidget = SampleApp.instance.qiscusMultichannelWidget

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // always call when active app
        if (qiscusMultichannelWidget.hasSetupUser()) {
            FirebaseServices().getCurrentDeviceToken()
        }

        login.setOnClickListener {
            if (qiscusMultichannelWidget.isLoggedIn()) {
                qiscusMultichannelWidget.clearUser()
                Toast.makeText(this, "Logout Success", Toast.LENGTH_LONG).show()

            } else {
                val username = etDisplayName.text.toString()
                val email = etUserEmail.text.toString()
                val avatarUrl =
                    "https://vignette.wikia.nocookie.net/fatal-fiction-fanon/images/9/9f/Doraemon.png/revision/latest?cb=20170922055255"

                if (isValidEmail(email)) {
                    val userProperties = mapOf(
                        "city" to "jogja",
                        "job" to "developer"
                    ) // userProperties are additional details of the user(optional)
//                    qiscusMultichannelWidget.setUser(email, username, avatarUrl, userProperties)

                    initChat(0)

                } else {
                    Toast.makeText(this, "Please check email format", Toast.LENGTH_LONG).show()
                }
            }
            setButton()
        }

        if (qiscusMultichannelWidget.isLoggedIn()) {
            qiscusMultichannelWidget.openChatRoom(this)
        }
    }

    private fun initChat(channelId: Int) {

        val username = etDisplayName.text.toString()
        val email = etUserEmail.text.toString()
        qiscusMultichannelWidget.setUser(
            email,
            username,
            ""
        )

        qiscusMultichannelWidget.initiateChat().setChannelId(125149).startChat(
            this,
            object : QiscusChatRoomBuilder.InitiateCallback {
                override fun onProgress() {

                }

                override fun onSuccess(qChatRoom: QChatRoom) {
                    Log.e("defg", "sukses")
                    MultichannelConst.qiscusCore()?.api?.getAllChatRooms(false, false, false, 1, 20)
                        ?.subscribeOn(Schedulers.io())
                        ?.observeOn(AndroidSchedulers.mainThread())
                        ?.subscribe({
                            Log.e("defg list messages", it.size.toString())
                        },{
                            Log.e("defg", it.message.toString())
                        })
                }

                override fun onError(throwable: Throwable) {

                }

            }
        )

        // only 1 after initiateChat
        if (qiscusMultichannelWidget.hasSetupUser()) {
            FirebaseServices().getCurrentDeviceToken()
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun setButton() {
        tv_start.text = if (qiscusMultichannelWidget.isLoggedIn()) "LOGOUT" else "START"
    }

    override fun onResume() {
        super.onResume()
        setButton()
    }

}
