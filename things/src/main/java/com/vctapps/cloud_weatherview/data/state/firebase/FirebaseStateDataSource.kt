package com.vctapps.cloud_weatherview.data.state.firebase

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vctapps.cloud_weatherview.data.state.StateDataSource
import com.vctapps.cloud_weatherview.domain.WeatherState
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe

class FirebaseStateDataSource(private val database: FirebaseDatabase): StateDataSource {

    override fun state(): Flowable<WeatherState> {
        return Flowable.create(firebaseSubscriber(), BackpressureStrategy.BUFFER)
    }

    private fun firebaseSubscriber() = FlowableOnSubscribe<WeatherState> {emitter ->
        val state = database.getReference("cloud")

        state.child("state").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Can't get values from realtime database")
                Log.e("Firebase", "${error.message}")
                emitter.onNext(WeatherState.ERROR)
            }

            override fun onDataChange(data: DataSnapshot) {
                val state: Int = data.getValue(Int::class.java) ?: -1
                Log.d("Firebase", "Get data $state")
                emitter.onNext(map(state))
            }
        })
    }

    private fun map(stateValue: Int) = when (stateValue) {
        0 -> WeatherState.CLEAR
        1 -> WeatherState.FEW_CLOUDS
        2 -> WeatherState.CLOUDS
        3 -> WeatherState.RAIN
        4 -> WeatherState.THUNDERSTORM
        else -> WeatherState.ERROR
    }

}