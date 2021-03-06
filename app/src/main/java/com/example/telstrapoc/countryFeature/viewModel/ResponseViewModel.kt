package com.example.telstrapoc.countryFeature.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.telstrapoc.countryFeature.CountryFeatureUsecase
import com.example.telstrapoc.executer.IExecuterThread
import com.example.telstrapoc.executer.UIThread
import com.example.telstrapoc.module.ThreadModule
import com.example.telstrapoc.utils.NetworkConnection
import rx.Subscriber

/*
View Model class for Response model
 */

class ResponseViewModel : ViewModel {

    private var title: String = " "
    var rows: ArrayList<CountryFeatureViewModel> = ArrayList()

    constructor() : super()

    var loadingError = MutableLiveData<Boolean>()
    var loading = MutableLiveData<Boolean>()
    var titleLiveData = MutableLiveData<String>()
    var rowsLiveData = MutableLiveData<ArrayList<CountryFeatureViewModel>>()
    private val uiThread: UIThread = ThreadModule().providePostExecutionThread()
    private val executorThread: IExecuterThread = ThreadModule().provideExecutorThread()

    private val countryFeatureUsecase: CountryFeatureUsecase =
        CountryFeatureUsecase(executorThread, uiThread)

    fun getCountryFeature() {
        loadingError.value = false
        loading.value = true
        countryFeatureUsecase.execute(CountryFeatureSubscriber())
    }

    /*
   Subscriber to show image list on UI
   as soon as image list downloads from server it get notifies and show list of images on UI
    */
    inner class CountryFeatureSubscriber : Subscriber<ResponseViewModel>() {

        override fun onCompleted() {}
        override fun onError(e: Throwable) {
            loading.value = false
            loadingError.value = true
            Log.e("TelstraPoc", e.toString())
        }

        override fun onNext(resDetails: ResponseViewModel) {
            rowsLiveData.value = resDetails.rows
            titleLiveData.value = resDetails.title
            loadingError.value = false
            loading.value = false
        }
    }
}
