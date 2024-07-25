package com.unsaapp.utilidades

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.unsaapp.R

class AdManager private constructor() {
    companion object {
        private var rewardedAd1: RewardedAd? = null
        private var rewardedAd2: RewardedAd? = null
        private const val TAG = "AdManager"

        fun loadRewardedAd(context: Context, adUnitId: String, imageView: ImageView, adIndex: Int) {
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(
                context,
                adUnitId,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d(TAG, adError.toString())
                        if (adIndex == 1) rewardedAd1 = null
                        else if (adIndex == 2) rewardedAd2 = null
                    }

                    override fun onAdLoaded(ad: RewardedAd) {
                        if (adIndex == 1) rewardedAd1 = ad
                        else if (adIndex == 2) rewardedAd2 = ad
                        (context as? Activity)?.runOnUiThread {
                            imageView.setImageResource(R.drawable.masint)
                        }

                        val fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdClicked() {
                                Log.d(TAG, "Ad was clicked.")
                            }

                            override fun onAdDismissedFullScreenContent() {
                                Utilidades.mostrarMsjCorto(context,"Obtuvo +5 Inst")

                                (context as? Activity)?.runOnUiThread {
                                    imageView.setImageResource(R.drawable.mas)
                                }
                            }

                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                Utilidades.mostrarMsjCorto(context,"Intente mas tarde")
                            }

                            override fun onAdImpression() {
                                // Called when an impression is recorded for an ad.
                            }

                            override fun onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                            }
                        }

                        if (adIndex == 1) rewardedAd1?.fullScreenContentCallback =
                            fullScreenContentCallback
                        else if (adIndex == 2) rewardedAd2?.fullScreenContentCallback =
                            fullScreenContentCallback
                    }
                })
        }

        fun mostrarAnuncioRecompensado(context: Context, numerito: TextView, adIndex: Int) {
            val rewardedAd = if (adIndex == 1) rewardedAd1 else rewardedAd2

            rewardedAd?.let { ad ->
                ad.show(context as Activity, OnUserEarnedRewardListener { rewardItem ->
                    var rewardAmount = rewardItem.amount
                    rewardAmount = 5
                    val sharedPreferences =
                        context.getSharedPreferences("Puntajes", Context.MODE_PRIVATE)
                    var valorActual1 = sharedPreferences.getInt("intentos", 0)
                    valorActual1 += rewardAmount

                    val editor = sharedPreferences.edit()
                    editor.putInt("intentos", valorActual1)
                    editor.apply()

                    (context as? Activity)?.runOnUiThread {
                        numerito.text = valorActual1.toString()
                    }

                    Log.d(TAG, "El usuario ganó la recompensa.")
                })
            } ?: run {
                Log.d(TAG, "El anuncio recompensado no estaba listo aún.")
            }
        }
    }
}
